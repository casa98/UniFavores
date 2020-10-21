package com.cagudeloa.unifavores.ui.addfavor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.AddFavorDialogBinding
import com.cagudeloa.unifavores.model.Favor
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddFavorFragment : BottomSheetDialogFragment() {

    private lateinit var binding: AddFavorDialogBinding
    private lateinit var viewModel: AddFavorViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_favor_dialog, container, false)
        viewModel = ViewModelProvider(this).get(AddFavorViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.askFavor.setOnClickListener {
            val title = binding.favorTitleEdit.text.toString().trim()
            val description = binding.favorDescriptionEdit.text.toString().trim()
            val location = binding.favorLocationEdit.text.toString().trim()
            if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
                showToast("Todos los campos son requeridos")
                return@setOnClickListener
            } else {
                binding.askFavor.visibility = View.INVISIBLE
                val favor = Favor()
                favor.favorTitle = title
                favor.favorDescription = description
                favor.favorLocation = location
                viewModel.addFavor(favor)
                showToast("Favor creado exitosamente")
            }
            // Close Dialog
            dismiss()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}