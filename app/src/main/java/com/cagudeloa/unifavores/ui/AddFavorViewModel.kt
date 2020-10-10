package com.cagudeloa.unifavores.ui

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cagudeloa.unifavores.NODE_FAVORS
import com.cagudeloa.unifavores.model.Favor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat

class AddFavorViewModel : ViewModel() {

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    fun addFavor(favor: Favor) {

        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_FAVORS)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser!!.uid
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["user"] = user
        hashMap["assignedUser"] = ""
        hashMap["favorTitle"] = favor.favorTitle
        hashMap["favorDescription"] = favor.favorDescription
        hashMap["favorLocation"] = favor.favorLocation
        hashMap["creationDate"] = convertLongToDateString(System.currentTimeMillis())
        hashMap["status"] = "0"

        databaseReference.push().setValue(hashMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _result.value = null    // Operation was successful
                } else {
                    _result.value = it.exception
                }
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDateString(sysTime: Long): String {
        return SimpleDateFormat("MMM-dd  HH:mm").format(sysTime).toString()
    }
}