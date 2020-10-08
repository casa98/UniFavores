package com.cagudeloa.unifavores.ui.favors

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.Favor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_favors.*

class FavorsFragment : Fragment(), FavorsAdapter.OnItemClickListener {

    private var favors = ArrayList<Favor>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_favors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Bring data from Firestore and setup adapter
        getUsersList()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    private fun getUsersList() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Favors")
        // This is not to show the favors made my myself
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser!!.uid
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Reload data
                favors.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val favor = dataSnapshot.getValue(Favor::class.java)
                    if (favor!!.user != currentUser) {     // Add to favors list, if I didn't request the favor
                        if (favor.status == "0") {    // It's unassigned
                            favors.add(favor)
                        }
                    }
                }
                favors.reverse()
                // Setup adapter
                val userAdapter = FavorsAdapter(requireContext(), this@FavorsFragment, favors)
                if (myRecyclerView != null)
                    myRecyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
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