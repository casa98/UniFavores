package com.cagudeloa.unifavores.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.auth.LoginActivity
import com.cagudeloa.unifavores.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var currentUser = FirebaseAuth.getInstance().currentUser!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.profile = viewModel
        binding.lifecycleOwner = this

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changePhotoButton.setOnClickListener {
            //Toast.makeText(requireContext(), "Sorry friend, not yet", Toast.LENGTH_SHORT).show()
            choosePicture()
        }

        viewModel.getProfileData()
        viewModel.eventSignOut.observe(viewLifecycleOwner) { signOut ->
            if (signOut) {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                viewModel.signOutEvent()
            }
        }
    }

    private fun choosePicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            uploadPicture(data.data!!)
            Picasso.get().load(data.data).error(R.drawable.ic_lock).into(binding.circleImageView3)
        }
    }

    private fun uploadPicture(data: Uri) {
        val riversRef =
            storageReference.child("images/${currentUser.uid}")
        riversRef.putFile(data)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Image uploaded :)", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error uploading pic", Toast.LENGTH_LONG)
                    .show()
            }

    }
}