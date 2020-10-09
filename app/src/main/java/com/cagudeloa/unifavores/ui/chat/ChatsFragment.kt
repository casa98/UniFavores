package com.cagudeloa.unifavores.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentChatsBinding
import com.cagudeloa.unifavores.model.Favor
import com.cagudeloa.unifavores.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private lateinit var currentUser: FirebaseUser
    private var chatsWith = ArrayList<String>()
    private var users = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false)

        /**
         * TODO
         * Go Favors in db, check all favors where [assignedUser] == [currentUser]  (that's the query)
         * Then check [user] of the returned value, you'll get some IDs, those are the users you currently have chats with
         */

        currentUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference = FirebaseDatabase.getInstance().getReference("Favors")
        databaseReference.orderByChild("assignedUser")
            .equalTo(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatsWith.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val favor = dataSnapshot.getValue(Favor::class.java)
                        if(favor!!.status.toInt() == -1)
                            chatsWith.add(favor.user)
                    }
                    // Search for chatsWith (Users) in users db (I want name)
                    val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (dataSnapshot: DataSnapshot in snapshot.children){
                                    val user = dataSnapshot.getValue((User::class.java))
                                    if(chatsWith.contains(user!!.uid))
                                        users.add(user)
                                }
                                // TODO Create RecyclerView with users in users arraylist
                                Log.i("USERS TO TALK TO", users.toString())
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        return binding.root
    }
}