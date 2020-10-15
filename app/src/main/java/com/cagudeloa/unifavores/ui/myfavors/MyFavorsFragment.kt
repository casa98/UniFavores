package com.cagudeloa.unifavores.ui.myfavors

import android.app.AlertDialog
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
import com.cagudeloa.unifavores.ASSIGNED
import com.cagudeloa.unifavores.COMPLETED
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.UNASSIGNED
import com.cagudeloa.unifavores.model.Favor
import kotlinx.android.synthetic.main.fragment_my_favors.*

class MyFavorsFragment : Fragment(), MyFavorsAdapter.OnItemClickListener {

    private lateinit var viewModel: MyFavorsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MyFavorsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_my_favors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchMyFavors()
        viewModel.favors.observe(viewLifecycleOwner) { myFavorsList ->
            if (!myFavorsList.isNullOrEmpty()) {
                myFavorsSecond.visibility = View.GONE
                myFavorsRecyclerView.visibility = View.VISIBLE
                if (isAdded) {
                    val myFavorsAdapter =
                        MyFavorsAdapter(
                            requireContext(),
                            this@MyFavorsFragment,
                            myFavorsList
                        )
                    myFavorsRecyclerView.adapter = myFavorsAdapter
                }
            } else {
                myFavorsRecyclerView.visibility = View.GONE
                myFavorsSecond.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        myFavorsRecyclerView.layoutManager =
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
            val builder = AlertDialog.Builder(requireContext())
            if (favor.status == UNASSIGNED) {
                builder.setTitle("¿Deseas cancelar y eliminar este favor?\n")
                builder.setNegativeButton("No", null)
                builder.setPositiveButton("Sí") { _, _ ->
                    // Go db delete this favor
                    viewModel.deleteFavor(favor)
                    viewModel.result.observe(this){
                        if(it == null){
                            Toast.makeText(
                                requireContext(),
                                "Favor eliminado",
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
            } else if (favor.status == ASSIGNED) {
                builder.setTitle("¿Deseas chatear con ${favor.assignedUsername}?\n")
                builder.setNegativeButton("No", null)
                builder.setPositiveButton("Sí") { _, _ ->
                    // Navigate to chat with favor.assignedUser
                    val bundle = Bundle()
                    bundle.putString("userID", favor.assignedUser)
                    bundle.putString("username", favor.assignedUsername)
                    findNavController().navigate(
                        R.id.action_myFavorsFragment_to_messagesFragment,
                        bundle
                    )
                }
            }
            builder.show()
        }
    }
}