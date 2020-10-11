package com.cagudeloa.unifavores.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result


    fun getUsersList() {
        auth = FirebaseAuth.getInstance().currentUser!!
        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_FAVORS)
        databaseReference.orderByChild("user")
            .equalTo(auth.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatsWith = ArrayList<String>()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val favor = dataSnapshot.getValue(Favor::class.java)
                        if (favor!!.status.toInt() == -1) {
                            chatsWith.add(favor.assignedUser)
                        }
                    }

                    FirebaseDatabase.getInstance().getReference(NODE_USERS)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val users = ArrayList<User>()
                                for (dataSnapshot: DataSnapshot in snapshot.children) {
                                    val user = dataSnapshot.getValue((User::class.java))
                                    if (chatsWith.contains(user!!.uid))
                                        users.add(user)
                                }
                                users.reverse()
                                _users.value = users
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}