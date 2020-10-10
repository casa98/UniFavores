package com.cagudeloa.unifavores.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentChatsBinding
import com.cagudeloa.unifavores.model.Favor
import com.cagudeloa.unifavores.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chats.*

class ChatsFragment : Fragment(), ChatsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentChatsBinding
    private lateinit var currentUser: FirebaseUser
    private var chatsWith = ArrayList<String>()
    private var users = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getUsersList()
    }

    private fun getUsersList() {
        /**
         * Go Favors in db, check all favors where {user} == {currentUser}  (that's the query)
         * Then check {assignedUser} of the returned value, you'll get some IDs, those are the users you currently have chats with
         */

        currentUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference = FirebaseDatabase.getInstance().getReference("Favors")
        databaseReference.orderByChild("user")
            .equalTo(currentUser.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatsWith.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val favor = dataSnapshot.getValue(Favor::class.java)
                        if (favor!!.status.toInt() == -1) {
                            chatsWith.add(favor.assignedUser)
                        }
                    }
                    chatsWith.distinct()
                    //Log.i("USERS MAKING ME FAVORS (ID)", chatsWith.toString())
                    // Search for chatsWith (Users) in users db (I want their names)
                    FirebaseDatabase.getInstance().getReference("Users")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                users.clear()
                                for (dataSnapshot: DataSnapshot in snapshot.children) {
                                    val user = dataSnapshot.getValue((User::class.java))
                                    if (chatsWith.contains(user!!.uid))
                                        users.add(user)
                                }
                                //Log.i("USERS MAKING ME FAVORS (names)", users.toString())
                                users.reverse()
                                // Send it to the RecyclerView
                                if (isAdded) {
                                    val chatsAdapter =
                                        ChatsAdapter(requireContext(), this@ChatsFragment, users)
                                    chatsRecyclerView.adapter = chatsAdapter
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun setupRecyclerView() {
        chatsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        chatsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onItemClick(user: User) {
        val bundle = Bundle()
        bundle.putString("user", user.uid)
        findNavController().navigate(R.id.messagesFragment, bundle)
    }
}