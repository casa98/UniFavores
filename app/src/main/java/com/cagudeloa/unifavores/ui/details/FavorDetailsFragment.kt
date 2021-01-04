package com.cagudeloa.unifavores.ui.details

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
import androidx.navigation.fragment.navArgs
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentFavorDetailsBinding
import com.cagudeloa.unifavores.domain.RetrofitInstance
import com.cagudeloa.unifavores.model.Favor
import com.cagudeloa.unifavores.model.NotificationData
import com.cagudeloa.unifavores.model.PushNotification
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavorDetailsFragment : Fragment() {

    private lateinit var favor: Favor
    private lateinit var binding: FragmentFavorDetailsBinding
    private lateinit var viewModel: FavorDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the arguments coming from the previous fragment using SafeArgs
        val args: FavorDetailsFragmentArgs by navArgs()
        favor = args.favor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favor_details, container, false)

        viewModel = ViewModelProvider(this).get(FavorDetailsViewModel::class.java)
        binding.favorCreatorText.text = favor.username
        binding.titleFavor.text = favor.favorTitle
        binding.descriptionFavor.text = favor.favorDescription
        binding.locationFavor.text = favor.favorLocation
        loadUserCreatorImage()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Current user is checking this favor and decided to do it
        binding.doFavorButton.setOnClickListener { doFavor ->
            doFavor.visibility = View.INVISIBLE
            viewModel.changeFavorToAssigned(favor)
            viewModel.result.observe(viewLifecycleOwner) {
                if (it != "") {    // Means the favor creator username is it


                    // Send a notification to favor.user
                    val topic = "/topics/${favor.user}"
                    PushNotification(
                        // Content of the notification
                        NotificationData(
                            "Hello, ${favor.username}",
                            "$it is making you a favor:\n${favor.favorTitle}"
                        ),
                        topic
                    ).also { data ->
                        // Send notification in background
                        sendNotification(data)
                    }

                    // Show a message
                    Snackbar.make(
                        view,
                        "Check this favor and chat with ${binding.favorCreatorText.text} going to Menu",
                        Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show()
                    binding.doFavorButton.visibility = View.GONE
                } else {
                    Toast.makeText(requireContext(), "Something went wrong :(", Toast.LENGTH_SHORT).show()
                    doFavor.visibility = View.INVISIBLE
                }
            }
        }
    }

    // Load favor creator image, which appears with the favor id
    private fun loadUserCreatorImage() {
        viewModel.loadFavorCreatorImage(favor.user)
        viewModel.image.observe(viewLifecycleOwner) { image ->
            if (image.isNotEmpty()) {
                Picasso.get().load(image)
                    .error(R.drawable.ic_person)
                    .into(binding.circleImageView)
            } else {
                binding.circleImageView.setImageResource(R.drawable.ic_big_person)
            }
        }
    }

    private fun sendNotification(notification: PushNotification) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (!response.isSuccessful) {
                    Log.i("Error in response", response.errorBody().toString())
                }
                // else: Log.i("All good", "Response: ${Gson().toJson(response)}")
            } catch (e: Exception) {
                Log.i("Error in try", e.toString())
            }
        }
    }
}