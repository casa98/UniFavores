 package com.cagudeloa.unifavores.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentChatBinding
import com.cagudeloa.unifavores.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

 class ChatFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var binding: FragmentChatBinding
    private var firebaseUser: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         requireArguments().let {
            userId = it.getString("user")!!
         }
         firebaseUser = FirebaseAuth.getInstance().currentUser
         databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
         databaseReference!!.addValueEventListener(object : ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                 (activity as AppCompatActivity).supportActionBar?.title = user!!.username
             }

             override fun onCancelled(error: DatabaseError) {

             }

         })
     }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.i("UID: ", userId)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)



        return binding.root
    }
 }