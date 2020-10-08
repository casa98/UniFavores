package com.cagudeloa.unifavores.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentAddFavorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat

class AddFavorFragment : Fragment() {

    private lateinit var binding: FragmentAddFavorBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_favor, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.askFavor.setOnClickListener {
            // TODO Checks fields are valid
            auth = FirebaseAuth.getInstance()
            val title = binding.favorTitleEdit.text.toString()
            val description = binding.favorDescriptionEdit.text.toString()
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Ambos campos son requeridos", Toast.LENGTH_LONG)
                    .show()
            } else {
                val user = auth.currentUser!!.uid
                val databaseReference = FirebaseDatabase.getInstance().getReference("Favors")
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["user"] = user
                hashMap["assignedUser"] = ""
                hashMap["favorTitle"] = title
                hashMap["favorDescription"] = description
                hashMap["creationDate"] = convertLongToDateString(System.currentTimeMillis())
                hashMap["status"] = "0"
                databaseReference.push().setValue(hashMap)
                    .addOnCompleteListener(requireActivity()) { result ->
                        if (result.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                " Favor creado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.navigation_home)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Este favor no se creó\nIntenta de nuevo",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDateString(sysTime: Long): String {
        return SimpleDateFormat("MMM-dd  HH:mm").format(sysTime).toString()
    }
}