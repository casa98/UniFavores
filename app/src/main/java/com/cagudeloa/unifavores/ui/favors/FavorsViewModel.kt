package com.cagudeloa.unifavores.ui.favors

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

class FavorsViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth
    private val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_FAVORS)

    private val _favors = MutableLiveData<ArrayList<Favor>>()
    val favors: LiveData<ArrayList<Favor>>
        get() = _favors

    fun fetchFavors() {
        // Need it not to show favors requested my myself
        auth = FirebaseAuth.getInstance()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val favors = ArrayList<Favor>()

                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val favor = dataSnapshot.getValue(Favor::class.java)
                        if (favor!!.user != auth.currentUser!!.uid && favor.status == "0")  // Not requested by me and unassigned
                            favor.let {
                                favors.add(it)
                            }
                    }
                    // Now the 'local' favors into the LiveData (I'll be observing it)
                    favors.reverse()    // To show first, the most recent one (not the best solution, I know)
                    _favors.value = favors
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}