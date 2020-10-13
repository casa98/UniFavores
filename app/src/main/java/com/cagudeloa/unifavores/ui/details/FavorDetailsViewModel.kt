package com.cagudeloa.unifavores.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cagudeloa.unifavores.*
import com.cagudeloa.unifavores.model.Favor
import com.cagudeloa.unifavores.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavorDetailsViewModel : ViewModel() {

    private var userID: String = FirebaseAuth.getInstance().currentUser!!.uid

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result


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
                    hashMap[FAVOR_ASSIGNED_USER] = FirebaseAuth.getInstance().currentUser!!.uid
                    hashMap[FAVOR_STATUS] = "-1"

                    val secondDBReference = FirebaseDatabase.getInstance().getReference(NODE_USERS)
                        .child(userID)
                    secondDBReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val username = snapshot.getValue(User::class.java)
                            hashMap[FAVOR_ASSIGNED_USERNAME] = username!!.username
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

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}