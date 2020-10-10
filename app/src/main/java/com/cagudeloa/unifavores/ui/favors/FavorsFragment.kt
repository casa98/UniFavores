package com.cagudeloa.unifavores.ui.favors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
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

        floatingActionButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.addFavor)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchFavors()
        viewModel.favors.observe(viewLifecycleOwner, { favorsList ->
            if (myRecyclerView != null) {
                val favorsAdapter = FavorsAdapter(requireContext(), this@FavorsFragment, favorsList)
                myRecyclerView.adapter = favorsAdapter
            }
        })
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

    override fun onItemClick(favor: Favor) {
        val bundle = Bundle()
        bundle.putParcelable("favor", favor)
        findNavController().navigate(R.id.action_navigation_home_to_favorDetailsFragment, bundle)
    }
}