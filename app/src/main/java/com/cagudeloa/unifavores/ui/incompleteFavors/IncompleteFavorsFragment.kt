package com.cagudeloa.unifavores.ui.incompleteFavors

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.Favor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_incomplete_favors.*

class IncompleteFavorsFragment : Fragment(), IncompleteFavorsAdapter.OnItemClickListener {

    private var favors = ArrayList<Favor>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_incomplete_favors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getUsersList()
    }

    private fun getUsersList() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Favors")
        // This is not to show the favors made my myself
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser!!.uid
        databaseReference.orderByChild("assignedUser")
            .equalTo(currentUser).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Reload data
                    favors.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val favor = dataSnapshot.getValue(Favor::class.java)
                        if (favor!!.status == "-1")
                            favors.add(favor)
                    }
                    favors.reverse()
                    // Setup adapter
                    if (isAdded) {
                        val userAdapter = IncompleteFavorsAdapter(
                            requireContext(),
                            this@IncompleteFavorsFragment,
                            favors
                        )
                        incompleteFavorsRecyclerView.adapter = userAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun setupRecyclerView() {
        incompleteFavorsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onItemClick(favor: Favor) {
        Log.i("CLICKED", favor.toString())
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
                        updateStatusInDatabase(favor)
                    }
                    secondBuilder.show()
                } else {
                    // Take me to chat with ${favor.user}
                    val bundle = Bundle()
                    bundle.putString("user", favor.user)
                    findNavController().navigate(
                        R.id.action_incompletFragment_to_messagesFragment,
                        bundle
                    )
                }

            }).show()
    }

    private fun updateStatusInDatabase(favor: Favor) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Favors")
        databaseReference.orderByChild("favorDescription")
            .equalTo(favor.favorDescription).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("FAVOR DATA", snapshot.toString())
                    val favorID = snapshot.children.iterator().next().key.toString()
                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["status"] = "1"     // Means it was completed
                    /**
                     * TODO WHY UPDATE AND NO DELETE?
                     * TODO I plan to tell user requester to reconfirm Favor was really completed, then delete it from DB
                     */
                    databaseReference.child(favorID).updateChildren(hashMap)

                    // TODO Update current user karma (score)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}