package com.cagudeloa.unifavores.ui.addfavor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.Favor
import kotlinx.android.synthetic.main.fragment_add_favor.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class AddFavorFragment : Fragment() {

    private lateinit var viewModel: AddFavorViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AddFavorViewModel::class.java)
        return inflater.inflate(R.layout.fragment_add_favor, container, false)
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
                val favor = Favor()
                favor.favorTitle = title
                favor.favorDescription = description
                favor.favorLocation = location
                viewModel.addFavor(favor)
            }
        }

        viewModel.result.observe(viewLifecycleOwner, {
            if (it == null) {
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
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        UIUtil.hideKeyboard(requireActivity())
    }
}