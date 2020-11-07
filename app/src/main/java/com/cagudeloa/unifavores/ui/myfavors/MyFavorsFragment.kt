package com.cagudeloa.unifavores.ui.myfavors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.ASSIGNED
import com.cagudeloa.unifavores.COMPLETED
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.UNASSIGNED
import com.cagudeloa.unifavores.databinding.FragmentMyFavorsBinding
import com.cagudeloa.unifavores.model.Favor
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyFavorsFragment : Fragment(), MyFavorsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentMyFavorsBinding
    private lateinit var viewModel: MyFavorsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_favors, container, false)
        viewModel = ViewModelProvider(this).get(MyFavorsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // All similar to most of views, check another packages if any doubts
        viewModel.fetchMyFavors()
        viewModel.favors.observe(viewLifecycleOwner) { myFavorsList ->
            if (!myFavorsList.isNullOrEmpty()) {
                binding.myFavorsSecond.visibility = View.GONE
                binding.myFavorsRecyclerView.visibility = View.VISIBLE
                if (isAdded) {
                    val myFavorsAdapter =
                        MyFavorsAdapter(
                            requireContext(),
                            this@MyFavorsFragment,
                            myFavorsList
                        )
                    binding.myFavorsRecyclerView.adapter = myFavorsAdapter
                }
            } else {
                binding.myFavorsRecyclerView.visibility = View.GONE
                binding.myFavorsSecond.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        binding.myFavorsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onItemClick(favor: Favor) {
        /**
         * Show dialog saying:
         * Delete favor (if favor is not assigned)
         * Chat with favor maker (if favor is assigned)
         *
         * Do nothing if favor is completed
         */
        if (favor.status != COMPLETED) {
            val builder = MaterialAlertDialogBuilder(requireContext())
            if (favor.status == UNASSIGNED) {
                builder.setTitle("Do you want to delete this favor?")
                builder.setMessage("You won't recover any points")
                builder.setNegativeButton("Yes", null)
                builder.setPositiveButton("No") { _, _ ->
                    // Go db delete this favor
                    viewModel.deleteFavor(favor)
                    viewModel.result.observe(viewLifecycleOwner) {
                        if (it == null)
                            showToast("Favor deleted")
                        else
                            showToast("Error: ${it.message}")
                    }
                }
            } else if (favor.status == ASSIGNED) {
                builder.setTitle("Do you want to chat with ${favor.assignedUsername}?\n")
                builder.setNegativeButton("No", null)
                builder.setPositiveButton("Yes") { _, _ ->
                    navigateToChat(favor)
                }
            }
            builder.show()
        }
    }

    private fun navigateToChat(favor: Favor) {
        // Navigate to chat with favor.assignedUser
        findNavController().navigate(
            MyFavorsFragmentDirections.actionMyFavorsFragmentToMessagesFragment(
                favor.assignedUser,
                favor.assignedUsername,
                favor.username
            )
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}