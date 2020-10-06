package com.cagudeloa.unifavores.ui.incompleteFavors

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.Favor
import com.cagudeloa.unifavores.ui.favors.FavorsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_favors.*
import kotlinx.android.synthetic.main.fragment_incomplete_favors.*

class IncompleteFavorsFragment : Fragment(), IncompleteFavorsAdapter.OnItemClickListener {

    private var favors = ArrayList<Favor>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_incomplete_favors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getUsersList()
    }

    private fun getUsersList(){
        val databaseReference = FirebaseDatabase.getInstance().getReference("Favors")
        // This is not to show the favors made my myself
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser!!.uid
        databaseReference.orderByChild("assignedUser")
            .equalTo(currentUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Reload data
                favors.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val favor = dataSnapshot.getValue(Favor::class.java)
                    favors.add(favor!!)
                }
                favors.reverse()
                // Setup adapter
                val userAdapter = IncompleteFavorsAdapter(requireContext(), this@IncompleteFavorsFragment, favors)
                if( incompleteFavorsRecyclerView!=null)
                    incompleteFavorsRecyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun setupRecyclerView(){
        incompleteFavorsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onItemClick(favor: Favor) {
        Log.i("CLICKED", favor.toString())
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("¿Qué deseas hacer?")
        builder.setItems(arrayOf("Marcar como completado","Chatear"), DialogInterface.OnClickListener { _, i ->
            if (i == 0){
                // Show a confirmation dialog
                val secondBuilder = AlertDialog.Builder(requireContext())
                secondBuilder.setTitle("¿Has completado este favor?")
                secondBuilder.setNegativeButton("No", null)
                secondBuilder.setPositiveButton("Sí") { _, _ ->
                    // TODO Go db and update this favor to status = 1 (completed)
                    Log.i("COMPLETED", "Update data in DB")
                }
                secondBuilder.show()
            }else{
                Log.i("CLICKED", "Take me to chat with this.favor requester")
            }

        }).show()
    }
}