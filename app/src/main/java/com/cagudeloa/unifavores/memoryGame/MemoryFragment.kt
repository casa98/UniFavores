package com.cagudeloa.unifavores.memoryGame

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cagudeloa.memorygame.BaseFragment
import com.cagudeloa.unifavores.MemoryGamesMainActivity
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentMemoryBinding
import kotlinx.coroutines.launch

class MemoryFragment : BaseFragment() {

    private lateinit var binding: FragmentMemoryBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_memory, container, false
        )

        val counter: Long =
            6000 // initial countDown will be 6 seconds, decreases one second per round
        val memoryGame = MemoryGame(
            binding,
            counter,
            activity as MemoryGamesMainActivity
        )
        memoryGame.setScores()
        binding.mainButton.setOnClickListener {
            binding.mainButton.visibility = View.INVISIBLE
            // Show images and countDown timer
            memoryGame.showAnimals()
            // Hide images after countDown reaches zero
            memoryGame.hideImages()
            memoryGame.setListeners()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences("com.cagudeloa.memorygame.score", 0)
        val data = sharedPref.getString("memory", "0")
        launch {
            context?.let {
                binding.invalidateAll()
                binding.highestScoreText.text = data
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val score = binding.highestScoreText.text.toString()
        sharedPref = requireActivity().getSharedPreferences("com.cagudeloa.memorygame.score", 0)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.apply {
            editor.putString("memory", score)
        }.apply()
    }
}