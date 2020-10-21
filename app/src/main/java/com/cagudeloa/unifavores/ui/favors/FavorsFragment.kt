package com.cagudeloa.unifavores.ui.favors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentFavorsBinding
import com.cagudeloa.unifavores.model.Favor

class FavorsFragment : Fragment(), FavorsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentFavorsBinding
    private lateinit var viewModel: FavorsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favors, container, false)
        viewModel = ViewModelProvider(this).get(FavorsViewModel::class.java)
        return binding.root
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
                binding.floatingActionButton.visibility = View.GONE
            else
                binding.floatingActionButton.visibility = View.VISIBLE
        }

        binding.floatingActionButton.setOnClickListener { askFavor ->
            askFavor.findNavController().navigate(
                FavorsFragmentDirections.actionNavHomeToAddFavor()
            )
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
                binding.fragmentFavorsSecond.visibility = View.GONE
                if (isAdded) {
                    val favorsAdapter =
                        FavorsAdapter(requireContext(), this@FavorsFragment, favorsList)
                    binding.myRecyclerView.adapter = favorsAdapter
                }
            } else {
                // No favors to display, display a message
                binding.fragmentFavorsSecond.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        binding.myRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.myRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    // Executed when user click on a favor, it'll redirect to FavorDetailsFragment and send the favor
    override fun onItemClick(favor: Favor) {
        findNavController().navigate(
            FavorsFragmentDirections.actionNavigationHomeToFavorDetailsFragment(
                // Pass data using SafeArgs
                favor
            )
        )
    }
}