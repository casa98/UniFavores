package com.cagudeloa.unifavores.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentStatisticsBinding
import com.cagudeloa.unifavores.model.User
import com.cagudeloa.unifavores.ui.chat.ChatsAdapter

class StatisticsFragment : Fragment(), ChatsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var viewModel: StatisticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)
        viewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.getUsersList()
        viewModel.users.observe(viewLifecycleOwner, { usersList ->
            if (isAdded) {
                val chatsAdapter =
                    ChatsAdapter(requireContext(), this@StatisticsFragment, usersList)
                binding.usersRecyclerView.adapter = chatsAdapter
            }
        })
    }

    private fun setupRecyclerView() {
        binding.usersRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.usersRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onItemClick(user: User) {
        // Here bc I'm reusing an adapter. Think of a better solution later :)
    }
}