package com.cagudeloa.unifavores.sequenceGame

import android.content.DialogInterface
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.cagudeloa.memorygame.Score
import com.cagudeloa.unifavores.MemoryGamesMainActivity
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentSequenceBinding

class SequenceGame(
    private var binding: FragmentSequenceBinding,
    private var activity: MemoryGamesMainActivity
) {
    // These 3 vars are used for the counter
    private val sequenceTimes = listOf(1000L, 600L, 400L, 300L, 200L, 100L)
    private var i = 0   // How long will squares be visible
    private lateinit var countDownTimer: CountDownTimer
    private var countDownInterval = 50L

    // View binding for Scores
    private val initialScore = "100"
    private val score = Score(initialScore, "0")

    private var howManySquares = 2
    private var tappedSquares = 0
    private val viewResources: List<TextView> = listOf(
        binding.squareText1,
        binding.squareText2,
        binding.squareText3,
        binding.squareText4,
        binding.squareText5,
        binding.squareText6,
        binding.squareText7,
        binding.squareText8,
        binding.squareText9,
        binding.squareText10,
        binding.squareText11,
        binding.squareText12,
        binding.squareText13,
        binding.squareText14,
        binding.squareText15,
        binding.squareText16,
        binding.squareText17,
        binding.squareText18,
        binding.squareText19,
        binding.squareText20,
        binding.squareText21,
        binding.squareText22,
        binding.squareText23,
        binding.squareText24,
        binding.squareText25,
        binding.squareText26,
        binding.squareText27,
        binding.squareText28,
        binding.squareText29,
        binding.squareText30,
        binding.squareText31,
        binding.squareText32,
        binding.squareText33,
        binding.squareText34,
        binding.squareText35,
        binding.squareText36
    )
    private val listNumbers: MutableList<Int> = (viewResources.indices).toMutableList()

    fun setScores() {
        binding.score = score
    }

    // Here is where some squares will go visible for 1-2 seconds
    fun showSquares() {
        howManySquares++
        tappedSquares = 0
        binding.invalidateAll()
        binding.playButton.visibility = View.INVISIBLE
        for (i in viewResources) {
            i.setBackgroundResource(R.color.tilesColor)
            i.isClickable = false
        }
        if (howManySquares <= (listNumbers.size / 2 + 1)) {
            listNumbers.shuffle()
            for (i in 0 until howManySquares) {
                viewResources[listNumbers[i]].setBackgroundResource(R.color.tileSelectedColor)
            }
        } else {
            // Already 20 items selected and well answered, start again
            howManySquares = 3
            listNumbers.shuffle()
            if (i < sequenceTimes.size - 1)
                i++

            countDownInterval = 200L
            for (i in 0 until howManySquares) {
                viewResources[listNumbers[i]].setBackgroundResource(R.color.tileSelectedColor)
            }
        }
        score.squares = "0 of $howManySquares"
    }

    // Here I'll hide the squares after 1-2 seconds
    fun hideSquares() {
        countDownTimer = object : CountDownTimer(sequenceTimes[i], countDownInterval) {
            override fun onTick(p0: Long) {
                for (i in viewResources) {
                    i.isClickable = false
                }
            }

            override fun onFinish() {
                for (i in viewResources) {
                    i.setBackgroundResource(R.color.tilesColor)
                    i.isClickable = true
                }
            }
        }
        countDownTimer.start()
    }

    fun setListeners() {
        for (item in viewResources) {
            item.setOnClickListener {
                if (tappedSquares <= howManySquares) {
                    val numberView = selectedView(item)
                    // Make the selected view, unclickable, no matter if correct or wrong choice
                    viewResources[numberView].isClickable = false
                    // Search in the first $howManySquares items of $listNumbers to know if this should have been selected, else, mark with X
                    if (listNumbers.indexOf(numberView) < howManySquares) {
                        //Log.v("testing", "good selection, paint as blue")
                        item.setBackgroundResource(R.color.tileSelectedColor)
                        // Update score
                        updateScore(5)
                        tappedSquares++
                        score.squares = "$tappedSquares of $howManySquares"
                    } else {
                        //Bad choice, mark with $numberView X
                        item.setBackgroundResource(R.drawable.x)
                        // Update score, if not leading not 0 or <0 result
                        if ((score.currentScore.toInt() - 50) > 0) {
                            updateScore(-50)
                        } else {
                            // Score reached or less, GAME OVER
                            binding.playButton.visibility = View.VISIBLE
                            gameOverDialog()
                            howManySquares = 2
                            i = 0
                            updateSquaresText()
                            for (i in viewResources) {
                                i.setBackgroundResource(R.color.tilesColor)
                                i.isClickable = false
                            }
                        }
                    }
                    //Log.v("testing", "Elements: $listNumbers")
                    //Log.v("testing", "Tapped view: $numberView")
                    if (tappedSquares == howManySquares) {
                        binding.invalidateAll()
                        score.squares = "Tap button to play"
                        binding.playButton.visibility = View.VISIBLE
                        for (i in viewResources) {
                            i.isClickable = false
                        }
                        if (score.currentScore.toInt() > score.highestScore.toInt()) {
                            binding.invalidateAll()
                            score.highestScore = score.currentScore
                        }
                        tappedSquares = 0
                        // Make textViews non-clickable to avoid unwanted behaviours
                    }
                }
            }
        }
    }

    private fun updateSquaresText() {
        binding.invalidateAll()
        score.currentScore = initialScore
        score.squares = "Tap button to play"
    }

    private fun updateScore(value: Int) {
        binding.invalidateAll()
        score.currentScore = (score.currentScore.toInt() + value).toString()
    }

    private fun gameOverDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Oh no")
        builder.setMessage("Game Over")
        builder.setPositiveButton("Play again") { _: DialogInterface, _ -> }
        builder.show()
    }

    private fun selectedView(v: TextView): Int {
        return when (v.id) {
            R.id.square_text1 -> 0
            R.id.square_text2 -> 1
            R.id.square_text3 -> 2
            R.id.square_text4 -> 3
            R.id.square_text5 -> 4
            R.id.square_text6 -> 5
            R.id.square_text7 -> 6
            R.id.square_text8 -> 7
            R.id.square_text9 -> 8
            R.id.square_text10 -> 9
            R.id.square_text11 -> 10
            R.id.square_text12 -> 11
            R.id.square_text13 -> 12
            R.id.square_text14 -> 13
            R.id.square_text15 -> 14
            R.id.square_text16 -> 15
            R.id.square_text17 -> 16
            R.id.square_text18 -> 17
            R.id.square_text19 -> 18
            R.id.square_text20 -> 19
            R.id.square_text21 -> 20
            R.id.square_text22 -> 21
            R.id.square_text23 -> 22
            R.id.square_text24 -> 23
            R.id.square_text25 -> 24
            R.id.square_text26 -> 25
            R.id.square_text27 -> 26
            R.id.square_text28 -> 27
            R.id.square_text29 -> 28
            R.id.square_text30 -> 29
            R.id.square_text31 -> 30
            R.id.square_text32 -> 31
            R.id.square_text33 -> 32
            R.id.square_text34 -> 33
            R.id.square_text35 -> 34
            else -> 35
        }
    }

}