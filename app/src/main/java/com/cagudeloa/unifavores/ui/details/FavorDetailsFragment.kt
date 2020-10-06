package com.cagudeloa.unifavores.ui.details

import android.opengl.Visibility
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
import androidx.navigation.fragment.findNavController
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentFavorDetailsBinding
import com.cagudeloa.unifavores.model.Favor
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
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
            // Add favor to the current user
            // Go to firebase and change favor status from 0 to -1 (assigned)
            // Get favorID:
            val databaseReference = FirebaseDatabase.getInstance().getReference("Favors")
            // Not the best query but I cannot use userID (because a user possibly create more than one favor)
            // This will fail if two user create a favor with same description
            // Why this? Firebase doesn't support multiple queries
            databaseReference.orderByChild("favorDescription")
                .equalTo(favor.favorDescription).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.i("FAVOR DATA", snapshot.toString())
                        val favorID = snapshot.children.iterator().next().key.toString()
                        Log.i("FAVOR ID", favorID)
                        // Change this snapshot (favor) by making status = -1 (means assigned)
                        val hashMap: HashMap<String, Any> = HashMap()
                        hashMap["status"] = "-1"
                        databaseReference.child(favorID).updateChildren(hashMap)
                            .addOnSuccessListener {
                                // Show a dialog rather, (in future)
                                Toast.makeText(
                                    requireContext(),
                                    "Chatea con ${binding.favorCreatorText.text} yendo al Menú",
                                    Toast.LENGTH_LONG
                                ).show()
                                binding.doFavorButton.visibility = View.GONE
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "No pudimos asignarte este favor, sorry :c", Toast.LENGTH_SHORT).show()
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cosmetic thing
        (activity as AppCompatActivity).supportActionBar?.elevation = 8.0F
    }
}