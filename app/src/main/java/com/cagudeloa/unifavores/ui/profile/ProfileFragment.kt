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

        /**
         * These two coming lines make the magic of LiveData and DataBinding
         * There are two LV variables in ViewModel I'm not observing (username, score)
         * Why? They talk directly to the XML, Check it and see how it sets text in those views
         */
        binding.profile = viewModel
        binding.lifecycleOwner = this

        // To save profile images that user selects
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child("images/")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load user info, it'll make username and score lv vars to take any value and update UI
        viewModel.getProfileData()
        // User tapped logout button
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
                    Picasso.get().load(image).placeholder(R.drawable.loading)
                        .error(R.drawable.ic_big_person)
                        .into(binding.profilePhoto)
                }
            } else {
                binding.profilePhoto.setImageResource(R.drawable.ic_big_person)
            }
        }
    }

    // Setup intent to select an image
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
            // User selected an image from gallery, set it on the view
            GlobalScope.launch(Dispatchers.Main) {
                Picasso.get().load(data.data).placeholder(R.drawable.loading)
                    .error(R.drawable.ic_big_person)
                    .into(binding.profilePhoto)
            }
        }
    }

    private fun uploadPicture(data: Uri) {
        // Full path of where the image will be located
        val ref = storageReference.child(currentUser.uid)
        val uploadTask = ref.putFile(data)
        /**
         * This coming in official doc, it'll upload the image and return me that image URL
         * so that I can save it into the User Model.
         */
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // This is the just uploaded image URL
                val downloadUri = task.result
                val hashMap: HashMap<String, Any> = HashMap()
                hashMap["image"] = downloadUri.toString()
                // Update model, include the image URL
                FirebaseDatabase.getInstance().reference.child(NODE_USERS).child(currentUser.uid)
                    .updateChildren(hashMap)
            }
        }

    }
}