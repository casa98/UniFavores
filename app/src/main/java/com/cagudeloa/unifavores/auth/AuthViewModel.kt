package com.cagudeloa.unifavores.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cagudeloa.unifavores.NODE_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth

    private val _loginSuccess = MutableLiveData<String?>()
    val loginSuccess: LiveData<String?>
        get() = _loginSuccess

    private val _registerSuccess = MutableLiveData<String?>()
    val registerSuccess: LiveData<String?>
        get() = _registerSuccess


    fun requestLogin(email: String, password: String) {
        // Request login to Firebase
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loginSuccess.value = null
            }
            .addOnFailureListener {
                _loginSuccess.value = it.message
            }
    }

    fun requestRegister(username: String, email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Create a collection whose ID is the userId (which is unique)
                val uid = auth.currentUser!!.uid

                val databaseReference =
                    FirebaseDatabase.getInstance().getReference(NODE_USERS).child(uid)
                // I'll save some data here to then, save in on db
                val hashMap: HashMap<String, Any> = HashMap()
                hashMap["uid"] = uid
                hashMap["username"] = username
                hashMap["image"] = ""
                hashMap["score"] = 2
                databaseReference.setValue(hashMap)
                    .addOnSuccessListener {
                        _registerSuccess.value = null
                    }
                    .addOnFailureListener {
                        _registerSuccess.value = it.message
                    }
            }
            .addOnFailureListener {
                _registerSuccess.value = it.message
            }
    }
}