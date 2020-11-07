package com.cagudeloa.memorygame.sequenceGame

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cagudeloa.memorygame.BaseFragment
import com.cagudeloa.unifavores.MemoryGamesMainActivity
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentSequenceBinding
import com.cagudeloa.unifavores.sequenceGame.SequenceGame
import kotlinx.coroutines.launch

class SequenceFragment : BaseFragment() {

    private lateinit var binding: FragmentSequenceBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sequence, container, false)

        val sequenceGame = SequenceGame(binding, activity as MemoryGamesMainActivity)
        sequenceGame.setScores()

        binding.playButton.setOnClickListener {
            sequenceGame.showSquares()
            sequenceGame.hideSquares()
            sequenceGame.setListeners()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences("com.cagudeloa.memorygame.score", 0)
        val data = sharedPref.getString("sequence", "0")
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
        editor.apply{
            editor.putString("sequence", score)
        }.apply()
    }
}