package com.cagudeloa.unifavores.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentAddFavorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat

class AddFavorFragment : Fragment() {

    private lateinit var binding: FragmentAddFavorBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_favor, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.askFavor.setOnClickListener {
            // TODO Checks fields are valid
            auth = FirebaseAuth.getInstance()
            // Use it to get the username from db (later)
            val user = auth.currentUser!!.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("Favors")
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["user"] = user
            hashMap["favorTitle"] = binding.favorTitleEdit.text.toString()
            hashMap["favorDescription"] = binding.favorDescriptionEdit.text.toString()
            hashMap["creationDate"] = convertLongToDateString(System.currentTimeMillis())
            hashMap["status"] = "0"
            databaseReference.push().setValue(hashMap).addOnCompleteListener(requireActivity()){ result ->
                if(result.isSuccessful){
                    findNavController().navigate(R.id.navigation_home)
                }else{
                    Toast.makeText(requireContext(), "Este favor no se creó :c\nIntenta de nuevo :)", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDateString(sysTime: Long): String {
        return SimpleDateFormat("MMM-dd  HH:mm").format(sysTime).toString()
    }
}