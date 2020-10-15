package com.cagudeloa.unifavores.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.cagudeloa.unifavores.NODE_USERS
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.auth.LoginActivity
import com.cagudeloa.unifavores.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
        storageReference = storage.reference.child("images/")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProfileData()
        viewModel.eventSignOut.observe(viewLifecycleOwner) { signOut ->
            if (signOut) {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                viewModel.signOutEvent()
            }
        }

        /**
         * Code for getting image from Gallery, uploading it to Storage, and show it in drawer
         */
        binding.changePhotoButton.setOnClickListener {
            choosePicture()
        }

        // Listen for image in ViewModel and set the pic
        viewModel.image.observe(viewLifecycleOwner) { image ->
            if (image.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.Main) {
                    Picasso.get().load(image).placeholder(R.drawable.loading).error(R.drawable.no_photo)
                        .into(binding.profilePhoto)
                }
            }else{
                binding.profilePhoto.setImageResource(R.drawable.no_photo)
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
            Picasso.get().load(data.data).placeholder(R.drawable.loading).error(R.drawable.no_photo)
                .into(binding.profilePhoto)
        }
    }

    private fun uploadPicture(data: Uri) {
        val ref = storageReference.child(currentUser.uid)
        val uploadTask = ref.putFile(data)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val hashMap: HashMap<String, Any> = HashMap()
                hashMap["image"] = downloadUri.toString()
                FirebaseDatabase.getInstance().reference.child(NODE_USERS).child(currentUser.uid)
                    .updateChildren(hashMap)
            }
        }

    }
}