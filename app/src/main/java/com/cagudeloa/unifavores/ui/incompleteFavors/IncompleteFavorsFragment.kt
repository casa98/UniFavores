package com.cagudeloa.unifavores.ui.incompleteFavors

import android.content.DialogInterface
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
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentIncompleteFavorsBinding
import com.cagudeloa.unifavores.model.Favor
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class IncompleteFavorsFragment : Fragment(), IncompleteFavorsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentIncompleteFavorsBinding
    private lateinit var viewModel: IncompleteFavorsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_incomplete_favors, container, false)
        viewModel = ViewModelProvider(this).get(IncompleteFavorsViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchIncompleteFavors()
        viewModel.favors.observe(viewLifecycleOwner) { incompleteFavorsList ->
            if (!incompleteFavorsList.isNullOrEmpty()) {
                binding.incompleteFavorsSecond.visibility = View.GONE
                binding.incompleteFavorsRecyclerView.visibility = View.VISIBLE
                if (isAdded) {
                    val incompleteFavorsAdapter =
                        IncompleteFavorsAdapter(
                            requireContext(),
                            this@IncompleteFavorsFragment,
                            incompleteFavorsList
                        )
                    binding.incompleteFavorsRecyclerView.adapter = incompleteFavorsAdapter
                }
            } else {
                binding.incompleteFavorsRecyclerView.visibility = View.GONE
                binding.incompleteFavorsSecond.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.incompleteFavorsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    /**
     * Dialogs for when user taps on a pending favor
     * One for choosing to Chat or Mark favor as completed
     * Another for confirming the favor completion
     */
    override fun onItemClick(favor: Favor) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("What do you want to do?")
        builder.setItems(
            arrayOf("  Mark as completed", "  Chat"),
            DialogInterface.OnClickListener { _, i ->
                if (i == 0) {
                    // Show a confirmation dialog
                    val secondBuilder = MaterialAlertDialogBuilder(requireContext())
                    secondBuilder.setTitle("Sure you have completed this favor?\n")
                    secondBuilder.setNegativeButton("Yes", null)
                    secondBuilder.setPositiveButton("No") { _, _ ->
                        // Go db and update this favor to status = 1 (completed)
                        viewModel.updateStatusInDatabase(favor)
                        viewModel.result.observe(viewLifecycleOwner) {
                            if (it == null)
                                showToast("Favor completed")
                            else
                                showToast("Error: ${it.message}")
                        }
                    }
                    secondBuilder.show()
                } else {
                    // Take me to chat with ${favor.user}
                    findNavController().navigate(
                        IncompleteFavorsFragmentDirections.actionIncompletFragmentToMessagesFragment(
                            favor.user,
                            favor.username,
                            favor.assignedUsername
                        )
                    )
                }
            }).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}