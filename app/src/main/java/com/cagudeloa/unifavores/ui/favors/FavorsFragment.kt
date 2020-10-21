package com.cagudeloa.unifavores.ui.favors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.Favor
import kotlinx.android.synthetic.main.fragment_favors.*

class FavorsFragment : Fragment(), FavorsAdapter.OnItemClickListener {

    private lateinit var viewModel: FavorsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(FavorsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_favors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded)
            setupRecyclerView()

        /**
         * The floatingActionButton to ask for new favors, will be visible
         * only if currentUser score > 0, so I need to check it out to know
         * whether to show it or not
         */
        viewModel.getUserScore()
        viewModel.canAskFavor.observe(viewLifecycleOwner) { canAskForFavor ->
            if (!canAskForFavor)
                floatingActionButton.visibility = View.GONE
            else
                floatingActionButton.visibility = View.VISIBLE
        }

        floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_addFavor)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Go Firebase and bring the favors to populate recycler view
        viewModel.fetchFavors()
        // Observe a LiveData arrayList to show favors list
        viewModel.favors.observe(viewLifecycleOwner) { favorsList ->
            if (!favorsList.isNullOrEmpty()) {
                // Hide message if there are favors to show
                fragmentFavorsSecond.visibility = View.GONE
                if (isAdded) {
                    val favorsAdapter =
                        FavorsAdapter(requireContext(), this@FavorsFragment, favorsList)
                    myRecyclerView.adapter = favorsAdapter
                }
            } else {
                // No favors to display, display a message
                fragmentFavorsSecond.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        myRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        myRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    // Executed when user click on a favor, it'll redirect to FavorDetailsFragment and send the favor
    override fun onItemClick(favor: Favor) {
        val bundle = Bundle()
        bundle.putParcelable("favor", favor)
        findNavController().navigate(R.id.action_navigation_home_to_favorDetailsFragment, bundle)
    }
}