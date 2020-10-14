package com.cagudeloa.unifavores.ui.myfavors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cagudeloa.unifavores.ASSIGNED
import com.cagudeloa.unifavores.COMPLETED
import com.cagudeloa.unifavores.NODE_FAVORS
import com.cagudeloa.unifavores.UNASSIGNED
import com.cagudeloa.unifavores.model.Favor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyFavorsViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth

    private val _favors = MutableLiveData<ArrayList<Favor>>()
    val favors: LiveData<ArrayList<Favor>>
        get() = _favors

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result


    fun fetchMyFavors() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_FAVORS)
            .orderByChild("user").equalTo(currentUser)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favors = ArrayList<Favor>()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val favor = dataSnapshot.getValue(Favor::class.java)
                    favor!!.let {
                        favor.status = when (favor.status) {
                            "0" -> UNASSIGNED
                            "-1" -> ASSIGNED
                            else -> COMPLETED
                        }
                        favors.add(it)
                    }
                }
                favors.reverse()
                _favors.value = favors
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun deleteFavor(favor: Favor) {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference(NODE_FAVORS).child(favor.key)
        databaseReference.removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    _result.value = null
                else
                    _result.value = it.exception
            }
    }
}
