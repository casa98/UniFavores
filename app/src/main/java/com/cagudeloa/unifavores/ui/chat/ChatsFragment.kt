package com.cagudeloa.unifavores.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentChatsBinding
import com.cagudeloa.unifavores.model.User
import kotlinx.android.synthetic.main.fragment_chats.*

class ChatsFragment : Fragment(), ChatsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentChatsBinding
    private lateinit var viewModel: ChatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false)
        viewModel = ViewModelProvider(this).get(ChatsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.getUsersList()
        viewModel.users.observe(viewLifecycleOwner, { usersList ->
            if (!usersList.isNullOrEmpty()) {
                binding.chatsSecond.visibility = View.GONE
                binding.chatsRecyclerView.visibility = View.VISIBLE
                if (isAdded) {
                    val chatsAdapter =
                        ChatsAdapter(requireContext(), this@ChatsFragment, usersList)
                    chatsRecyclerView.adapter = chatsAdapter
                }
            } else {
                binding.chatsRecyclerView.visibility = View.GONE
                binding.chatsSecond.visibility = View.VISIBLE
            }
        })
    }

    private fun setupRecyclerView() {
        chatsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        chatsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onItemClick(user: User) {
        val bundle = Bundle()
        bundle.putString("user", user.uid)
        findNavController().navigate(R.id.messagesFragment, bundle)
    }
}