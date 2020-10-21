package com.cagudeloa.unifavores.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentMessagesBinding
import com.cagudeloa.unifavores.domain.RetrofitInstance
import com.cagudeloa.unifavores.model.NotificationData
import com.cagudeloa.unifavores.model.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class MessagesFragment : Fragment() {

    private lateinit var userID: String
    private lateinit var username: String
    private lateinit var otherUsername: String
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var viewModel: MessagesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Get here via arguments the favor creator ID,
         * so that the conversation will be with it and current user
         */
        val args: MessagesFragmentArgs by navArgs()
        userID = args.userID
        username = args.username
        otherUsername = args.otherUsername
        (activity as AppCompatActivity).supportActionBar?.title = username
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages, container, false)
        viewModel = ViewModelProvider(this).get(MessagesViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup RecyclerView
        setupRecyclerView()

        // Load messages of currentUser and userID
        viewModel.readMessage(userID)
        viewModel.messages.observe(viewLifecycleOwner) { messagesList ->
            if (!messagesList.isNullOrEmpty()) {
                binding.chatsLayout.visibility = View.GONE
                if (isAdded) {
                    val chatAdapter = MessagesAdapter(requireContext(), messagesList)
                    binding.apply {
                        chatRecyclerView.adapter = chatAdapter
                        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }
            } else {
                binding.chatsLayout.visibility = View.VISIBLE
            }
        }

        // [Send Message] button listener
        binding.sendMessageButton.setOnClickListener {
            val message = binding.messageEdit.text.toString()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(userID, message)

                // Which user the notification will be sent to? userID
                val topic = "/topics/${userID}"
                PushNotification(
                    // Content of the notification
                    NotificationData(otherUsername, message),
                    topic
                ).also {
                    // Send notification in background
                    sendNotification(it)
                }

                binding.messageEdit.setText("")
                binding.messageEdit.hint = getString(R.string.type_a_message)
            } else {
                binding.messageEdit.hint = "Escribe algo ombe"
            }
        }
    }

    private fun sendNotification(notification: PushNotification) =
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

    private fun setupRecyclerView() {
        binding.chatRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    // Hide keyboard if chat is abandoned and it's open
    override fun onDestroy() {
        super.onDestroy()
        UIUtil.hideKeyboard(requireActivity())
    }
}