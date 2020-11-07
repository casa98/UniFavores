package com.cagudeloa.unifavores

import android.content.ActivityNotFoundException
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.cagudeloa.memorygame.BaseFragment
import com.cagudeloa.unifavores.databinding.FragmentMainBinding

import kotlinx.coroutines.launch

class MainFragment : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentMainBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.clear_scores_menu -> {
                eraseValuesFromDB("memory")
                eraseValuesFromDB("sequence")
                binding.invalidateAll()
                binding.scoreView.text = "0"
                Toast.makeText(context, "Both scores cleared", Toast.LENGTH_LONG).show()
            }
            R.id.clear_memory_menu -> {
                eraseValuesFromDB("memory")
                getScoresFromDB()
                Toast.makeText(context, "Memory Game score cleared", Toast.LENGTH_LONG).show()
            }
            R.id.clear_sequence_menu -> {
                eraseValuesFromDB("sequence")
                getScoresFromDB()
                Toast.makeText(context, "Sequence Game score cleared", Toast.LENGTH_LONG).show()
            }
            R.id.share -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onShare() {
        sharedPref = requireActivity().getSharedPreferences("com.cagudeloa.memorygame.score", 0)
        val value1 = sharedPref.getString("memory", "0")
        val value2 = sharedPref.getString("sequence", "0")
        val shareIntent = ShareCompat.IntentBuilder.from(requireActivity())
            .setText(getString(R.string.share_text, binding.scoreView.text.toString(), value1, value2))
            .setType("text/plain")
            .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(requireActivity(), "Couldn't share, try later :)", Toast.LENGTH_LONG).show()
        }
    }

    private fun eraseValuesFromDB(id: String){
        sharedPref = requireActivity().getSharedPreferences("com.cagudeloa.memorygame.score", 0)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.apply{
            editor.putString(id, "0")
        }.apply()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getScoresFromDB()
        navController = Navigation.findNavController(view)
        binding.memoryButton.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_memoryFragment)
        }
        binding.sequenceButton.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_sequenceFragment)
        }
    }

    private fun getScoresFromDB(){
        sharedPref = requireActivity().getSharedPreferences("com.cagudeloa.memorygame.score", 0)
        launch {
            context?.let {
                val score1: String = sharedPref.getString("memory", "0")!!
                val score2: String = sharedPref.getString("sequence", "0")!!
                val data: String = (score1.toInt()+score2.toInt()).toString()
                binding.invalidateAll()
                binding.scoreView.text = data
            }
        }
    }
}
