package com.cagudeloa.unifavores.ui.incompleteFavors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cagudeloa.unifavores.FAVOR_DESCRIPTION
import com.cagudeloa.unifavores.NODE_FAVORS
import com.cagudeloa.unifavores.NODE_USERS
import com.cagudeloa.unifavores.model.Favor
import com.cagudeloa.unifavores.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class IncompleteFavorsViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth

    private val _favors = MutableLiveData<ArrayList<Favor>>()
    val favors: LiveData<ArrayList<Favor>>
        get() = _favors

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result


    fun fetchIncompleteFavors() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_FAVORS)
        databaseReference.orderByChild("assignedUser")
            .equalTo(currentUser).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val favors = ArrayList<Favor>()
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            val favor = dataSnapshot.getValue(Favor::class.java)
                            if (favor!!.status == "-1")
                                favor.let {
                                    favors.add(it)
                                }
                        }
                        favors.reverse()
                        _favors.value = favors
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun updateStatusInDatabase(favor: Favor) {
        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_FAVORS)
        databaseReference.orderByChild(FAVOR_DESCRIPTION)
            .equalTo(favor.favorDescription).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorID = snapshot.children.iterator().next().key.toString()
                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["status"] = "1"     // Means it was completed (check Model)
                    databaseReference.child(favorID).updateChildren(hashMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                updateUserScore()
                            } else {
                                _result.value = it.exception
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateUserScore() {
        auth = FirebaseAuth.getInstance()
        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_USERS)
            .child(auth.currentUser!!.uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUserData = snapshot.getValue(User::class.java)
                val hashMap: HashMap<String, Any> = HashMap()
                hashMap["score"] = currentUserData!!.score + 2
                databaseReference.updateChildren(hashMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful)
                            _result.value = null    // Operation was successful
                        else
                            _result.value = it.exception
                    }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


}