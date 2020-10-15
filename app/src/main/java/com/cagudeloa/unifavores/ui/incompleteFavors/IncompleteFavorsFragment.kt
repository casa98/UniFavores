package com.cagudeloa.unifavores.ui.incompleteFavors

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.Favor
import kotlinx.android.synthetic.main.fragment_incomplete_favors.*

class IncompleteFavorsFragment : Fragment(), IncompleteFavorsAdapter.OnItemClickListener {

    private lateinit var viewModel: IncompleteFavorsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(IncompleteFavorsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_incomplete_favors, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchIncompleteFavors()
        viewModel.favors.observe(viewLifecycleOwner) { incompleteFavorsList ->
            if (!incompleteFavorsList.isNullOrEmpty()) {
                incompleteFavorsSecond.visibility = View.GONE
                incompleteFavorsRecyclerView.visibility = View.VISIBLE
                if (isAdded) {
                    val incompleteFavorsAdapter =
                        IncompleteFavorsAdapter(
                            requireContext(),
                            this@IncompleteFavorsFragment,
                            incompleteFavorsList
                        )
                    incompleteFavorsRecyclerView.adapter = incompleteFavorsAdapter
                }
            } else {
                incompleteFavorsRecyclerView.visibility = View.GONE
                incompleteFavorsSecond.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        incompleteFavorsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    /**
     * Dialogs for when user taps on a pending favor
     * One for choosing to Chat or Mark favor as completed
     * Another for confirming the favor completion
     */
    override fun onItemClick(favor: Favor) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("¿Qué deseas hacer?")
        builder.setItems(
            arrayOf("  Marcar como completado", "  Chatear"),
            DialogInterface.OnClickListener { _, i ->
                if (i == 0) {
                    // Show a confirmation dialog
                    val secondBuilder = AlertDialog.Builder(requireContext())
                    secondBuilder.setTitle("¿Seguro que has completado este favor?\n")
                    secondBuilder.setNegativeButton("No", null)
                    secondBuilder.setPositiveButton("Sí") { _, _ ->
                        // Go db and update this favor to status = 1 (completed)
                        viewModel.updateStatusInDatabase(favor)
                        viewModel.result.observe(viewLifecycleOwner) {
                            if (it == null) {
                                Toast.makeText(
                                    requireContext(),
                                    "Favor completado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Error: ${it.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    secondBuilder.show()
                } else {
                    // Take me to chat with ${favor.user}
                    val bundle = Bundle()
                    bundle.putString("userID", favor.user)
                    bundle.putString("username", favor.username)
                    findNavController().navigate(
                        R.id.action_incompletFragment_to_messagesFragment,
                        bundle
                    )
                }
            }).show()
    }
}