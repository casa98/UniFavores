package com.cagudeloa.unifavores.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cagudeloa.unifavores.NODE_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileViewModel: ViewModel() {

    private var _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _score = MutableLiveData<String>()
    val score: LiveData<String>
        get() = _score

    private val _image = MutableLiveData<String>()
    val image: LiveData<String>
    get() = _image

    private val _eventSignOut = MutableLiveData<Boolean>()
    val eventSignOut : LiveData<Boolean>
        get() = _eventSignOut

    init {
        _username.value = ""
        _score.value = ""
        _image.value = ""
        _eventSignOut.value = false
    }


    fun getProfileData() {
        // [favor.user] tells me who created the current favor, get the username of that user and display it
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference =
            FirebaseDatabase.getInstance().getReference(NODE_USERS).child(user)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorCreator = snapshot.child("username").value.toString()
                val score = snapshot.child("score").value.toString()
                val image = snapshot.child("image").value.toString()
                // These 2 values are directly connected with the XML
                _username.value = favorCreator
                _score.value = score
                // This one is listened from the fragment and set using Picasso
                _image.value = image
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun signOut(){
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        _eventSignOut.value = true
    }

    fun signOutEvent(){
        _eventSignOut.value = false
    }

}