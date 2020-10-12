package com.cagudeloa.unifavores.ui.addfavor

import android.annotation.SuppressLint
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
import java.text.SimpleDateFormat

class AddFavorViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    fun addFavor(favor: Favor) {

        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_FAVORS)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser!!.uid
        val hashMap: HashMap<String, String> = HashMap()
        hashMap[FAVOR_USER] = user
        hashMap[FAVOR_ASSIGNED_USER] = ""
        hashMap[FAVOR_TITLE] = favor.favorTitle
        hashMap[FAVOR_DESCRIPTION] = favor.favorDescription
        hashMap[FAVOR_LOCATION] = favor.favorLocation
        hashMap[FAVOR_CREATION_DATE] = convertLongToDateString(System.currentTimeMillis())
        hashMap[FAVOR_STATUS] = "0"

        databaseReference.push().setValue(hashMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Update user score
                    updateUserScore()

                } else {
                    _result.value = it.exception
                }
            }
    }

    private fun updateUserScore() {
        auth = FirebaseAuth.getInstance()
        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_USERS)
            .child(auth.currentUser!!.uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUserData = snapshot.getValue(User::class.java)
                val hashMap: HashMap<String, Any> = HashMap()
                hashMap["score"] = currentUserData!!.score - 2
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

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDateString(sysTime: Long): String {
        return SimpleDateFormat("MMM-dd  HH:mm").format(sysTime).toString()
    }
}