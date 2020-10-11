package com.cagudeloa.unifavores.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cagudeloa.unifavores.NODE_FAVORS
import com.cagudeloa.unifavores.model.Favor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavorDetailsViewModel : ViewModel() {

    private var _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    init {
        _username.value = ""
    }


    fun getFavorCreator(favorUser: String) {
        // [favor.user] tells me who created the current favor, get the username of that user and display it
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(favorUser)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorCreator = snapshot.child("username").value.toString()
                _username.value = favorCreator
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun changeFavorToAssigned(favor: Favor) {
        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_FAVORS)
        /**
         * TODO
         * Not the best query but I cannot use userID (because a user possibly create more than one favor),
         * This will fail (using favorDescription as key) if two users create a favor with same description
         * Why this? Firebase doesn't support multiple queries. Find a solution
         */
        databaseReference.orderByChild("favorDescription")
            .equalTo(favor.favorDescription)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorID = snapshot.children.iterator().next().key.toString()
                    // Change this snapshot (favor) by making status = -1 (means assigned)
                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["assignedUser"] = FirebaseAuth.getInstance().currentUser!!.uid
                    hashMap["status"] = "-1"
                    databaseReference.child(favorID).updateChildren(hashMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                _result.value = null    // Success
                            } else {
                                _result.value = it.exception
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}