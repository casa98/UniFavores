package com.cagudeloa.unifavores.ui.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onDestroyView() {
        super.onDestroyView()
        // Cosmetic thing
        (activity as AppCompatActivity).supportActionBar?.elevation = 8.0F
    }
}