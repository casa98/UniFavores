package com.cagudeloa.unifavores.ui.details

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentFavorDetailsBinding
import com.cagudeloa.unifavores.model.Favor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavorDetailsFragment : Fragment() {

    private lateinit var favor: Favor
    private lateinit var binding: FragmentFavorDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the arguments coming from the previous fragment
        requireArguments().let {
            favor = it.getParcelable("favor")!!
        }
    }

    private fun getFavorCreator(binding: FragmentFavorDetailsBinding){
        // [favor.user] tells me who created the current favor, get the username of that user and display it
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(favor.user)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorCreator = snapshot.child("username").value.toString()
                binding.favorCreatorText.text = favorCreator
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.elevation = 0.0F
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favor_details, container, false)
        getFavorCreator(binding)
        binding.titleFavor.text = favor.favorTitle
        binding.descriptionFavor.text = favor.favorDescription

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.doFavorButton.setOnClickListener {
            if(binding.doFavorButton.text == getString(R.string.realizar_favor)){
                // Add favor to the current user
                binding.doFavorButton.text = getString(R.string.go_to_chat)
                Toast.makeText(requireContext(), "Favores pendientes --> Menú", Toast.LENGTH_SHORT).show()
                // Go to firebase and change favor status from 0 to -1 (assigned)
                // Get favorID:
                val databaseReference = FirebaseDatabase.getInstance().getReference("Favors")
                databaseReference.orderByChild("user")
                    .equalTo(favor.user).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val favorID = snapshot.children.iterator().next().key.toString()
                            // TODO Delete favor whose ID is {favorID}
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }else{
                // Take the current user to the chat with the favor requester
                Log.d("CHAT", "Go to chat")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cosmetic thing
        (activity as AppCompatActivity).supportActionBar?.elevation = 8.0F
    }
}