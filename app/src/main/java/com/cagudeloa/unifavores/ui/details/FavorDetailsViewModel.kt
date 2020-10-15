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

    private val _image = MutableLiveData<String>()
    val image: LiveData<String>
        get() = _image


    fun changeFavorToAssigned(favor: Favor) {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference(NODE_FAVORS).child(favor.key)
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap[FAVOR_ASSIGNED_USER] = FirebaseAuth.getInstance().currentUser!!.uid
        hashMap[FAVOR_STATUS] = "-1"
        val secondDBReference =
            FirebaseDatabase.getInstance().getReference(NODE_USERS).child(userID)
        secondDBReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.getValue(User::class.java)
                hashMap[FAVOR_ASSIGNED_USERNAME] = username!!.username
                databaseReference.updateChildren(hashMap)
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

    fun loadFavorCreatorImage(userID: String) {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference(NODE_USERS).child(userID)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.getValue(User::class.java)
                _image.value = username!!.image
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}