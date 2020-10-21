package com.cagudeloa.unifavores.ui.addfavor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.Favor
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.add_favor_dialog.*

class AddFavorFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: AddFavorViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AddFavorViewModel::class.java)
        return inflater.inflate(R.layout.add_favor_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        askFavor.setOnClickListener {
            val title = favorTitleEdit.text.toString().trim()
            val description = favorDescriptionEdit.text.toString().trim()
            val location = favorLocationEdit.text.toString().trim()
            if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Todos los campos son requeridos",
                    Toast.LENGTH_LONG
                )
                    .show()
                return@setOnClickListener
            } else {
                askFavor.visibility = View.INVISIBLE
                val favor = Favor()
                favor.favorTitle = title
                favor.favorDescription = description
                favor.favorLocation = location
                viewModel.addFavor(favor)
                Toast.makeText(requireContext(), " Favor creado exitosamente", Toast.LENGTH_SHORT)
                    .show()
            }
            // Close Dialog
            dismiss()
        }
    }
}