package com.cagudeloa.unifavores.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.auth.LoginActivity
import com.cagudeloa.unifavores.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.profile = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changePhotoButton.setOnClickListener {
            Toast.makeText(requireContext(), "Sorry friend, not yet", Toast.LENGTH_SHORT).show()
        }

        viewModel.getProfileData()
        viewModel.eventSignOut.observe(viewLifecycleOwner) { signOut ->
            if (signOut) {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                viewModel.signOutEvent()
            }
        }
    }
}