package com.cagudeloa.unifavores.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cagudeloa.unifavores.FAVOR_USER
import com.cagudeloa.unifavores.NODE_FAVORS
import com.cagudeloa.unifavores.NODE_USERS
import com.cagudeloa.unifavores.model.Favor
import com.cagudeloa.unifavores.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatsViewModel : ViewModel() {

    private lateinit var auth: FirebaseUser
    private val _users = MutableLiveData<ArrayList<User>>()
    val users: LiveData<ArrayList<User>>
        get() = _users


    fun getUsersList() {
        /**
         * The most confusing function till the moment (It was edited, was even worst)
         * Which chats will user see?
         * First, take only favors where status == -1 (active, assigned)
         * Then get favor when currentUser is creator or is making it
         */
        auth = FirebaseAuth.getInstance().currentUser!!
        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_FAVORS)
        databaseReference.orderByChild("status")
            .equalTo("-1").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Here I'm getting only assigned favors, the ony ones I care for chatting
                    val chatsWith = ArrayList<String>()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val favor = dataSnapshot.getValue(Favor::class.java)!!
                        // Favors i requested, add the assignedUser that is making it
                        if (favor.user == auth.uid) {
                            chatsWith.add(favor.assignedUser)
                        }
                        // Favors I'm doing, add users who requested them
                        if (favor.assignedUser == auth.uid) {
                            chatsWith.add(favor.user)
                        }
                    }

                    FirebaseDatabase.getInstance().getReference(NODE_USERS)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val users = ArrayList<User>()
                                for (dataSnapshot: DataSnapshot in snapshot.children) {
                                    val user = dataSnapshot.getValue((User::class.java))
                                    if (chatsWith.contains(user!!.uid)) {
                                        // TODO Change user.score depending on if currentUser is making favor, or the other thing
                                        // To show it instead of the score, Score will shown in StatisticsFragment
                                        users.add(user)
                                    }
                                }
                                users.toList().reversed()
                                _users.value = users
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}