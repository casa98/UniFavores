package com.cagudeloa.unifavores.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cagudeloa.unifavores.NODE_USERS
import com.cagudeloa.unifavores.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StatisticsViewModel : ViewModel() {

    private lateinit var auth: FirebaseUser
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users


    fun getUsersList() {
        auth = FirebaseAuth.getInstance().currentUser!!
        FirebaseDatabase.getInstance().getReference(NODE_USERS)
            .orderByChild("score")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = ArrayList<User>()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val user = dataSnapshot.getValue((User::class.java))
                        users.add(user!!)
                    }
                    // Sort users list by its score in desc order
                    val sortedUsers = users.sortedWith(compareBy { it.score }).reversed()
                    _users.value = sortedUsers
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}