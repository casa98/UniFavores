package com.cagudeloa.unifavores.ui.details

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
import com.cagudeloa.unifavores.databinding.FragmentFavorDetailsBinding
import com.cagudeloa.unifavores.model.Favor
import com.google.android.material.snackbar.Snackbar

class FavorDetailsFragment : Fragment() {

    private lateinit var favor: Favor
    private lateinit var binding: FragmentFavorDetailsBinding
    private lateinit var viewModel: FavorDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the arguments coming from the previous fragment
        requireArguments().let {
            favor = it.getParcelable("favor")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favor_details, container, false)

        viewModel = ViewModelProvider(this).get(FavorDetailsViewModel::class.java)
        binding.favorCreatorText.text = favor.username
        binding.titleFavor.text = favor.favorTitle
        binding.descriptionFavor.text = favor.favorDescription
        binding.locationFavor.text = favor.favorLocation


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.doFavorButton.setOnClickListener {
            viewModel.changeFavorToAssigned(favor)
            viewModel.result.observe(viewLifecycleOwner) {
                if (it == null) {
                    Snackbar.make(
                        view,
                        "Revisa este favor y chatea con ${binding.favorCreatorText.text} yendo al Menú",
                        Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show()
                    binding.doFavorButton.visibility = View.GONE
                } else {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}