package com.cagudeloa.unifavores.memoryGame

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.cagudeloa.memorygame.Score
import com.cagudeloa.unifavores.MemoryGamesMainActivity
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentMemoryBinding

class MemoryGame(
    private var binding: FragmentMemoryBinding,
    private var mainCounter: Long,
    private var activity: MemoryGamesMainActivity
) {
    // These 3 vars are used for the counter
    private var initialCountDown = mainCounter
    private lateinit var countDownTimer: CountDownTimer
    private val countDownInterval: Long = 1000

    // View binding for Scores
    private val initialScore = "100"
    private val score = Score(initialScore, "0")

    // Other values
    private var imageCounter: Int =
        0   // Tell me when all imageViews were selected, go to next round
    private var animalLocation = 0
    private var isFirstImage =
        true     // True if an image was selected and waiting for another to be selected
    private var currentSecondImage =
        0  // I save here the image I expect user selects in the second selected image
    private var alreadySelected = 0
    private var animation = true

    // 12 numbers for 12 imageView, these are randomize so that my animals go to different positions
    // How do I position the images in my 12 imageViews?
    // Let's say: listNumbers = [3 7 1 6 8 9...]
    // One of the animals goes to positions (3,7), other to (1,6), and so on...
    // Which animals do I choose? selectedAnimal[] at positions 1 3 5 7 9 11 (taken from animals[])
    private val listNumbers: MutableList<Int> = (1..12).toMutableList()

    // To randomize my 13 animals, so that they vary
    private val selectedAnimal: MutableList<Int> = (1..13).toMutableList()

    // Here are my animals
    private val animals = listOf(
        R.drawable.butterfly,
        R.drawable.color_parrot,
        R.drawable.dog,
        R.drawable.elephant,
        R.drawable.iguana,
        R.drawable.jellyfish,
        R.drawable.jiraffe,
        R.drawable.leopard,
        R.drawable.lion,
        R.drawable.parrot,
        R.drawable.rabbit,
        R.drawable.sharks,
        R.drawable.white_dog,

        R.drawable.select,      // To use it when an image is selected
        R.drawable.incorrect   // Tp use it when a couple of animals don't match
    )

    // My imageViews
    private val imageResources: List<ImageView> = listOf(
        binding.image1,
        binding.image2,
        binding.image3,
        binding.image4,
        binding.image5,
        binding.image6,
        binding.image7,
        binding.image8,
        binding.image9,
        binding.image10,
        binding.image11,
        binding.image12
    )

    fun setScores() {
        binding.score = score
    }

    fun showAnimals() {
        animation = true
        imageCounter = 0
        listNumbers.shuffle()   // randomize my list
        selectedAnimal.shuffle()
        for (i in 1..11 step 2) {    // Each of the image places
            chooseImageLocation(listNumbers[i - 1], selectedAnimal[i] - 1)
            chooseImageLocation(listNumbers[i], selectedAnimal[i] - 1)
        }
        animation = false
    }

    private fun chooseImageLocation(index: Int, selectedImage: Int) {
        val drawableResource = when (index) {
            1 -> imageResources[0]
            2 -> imageResources[1]
            3 -> imageResources[2]
            4 -> imageResources[3]
            5 -> imageResources[4]
            6 -> imageResources[5]
            7 -> imageResources[6]
            8 -> imageResources[7]
            9 -> imageResources[8]
            10 -> imageResources[9]
            11 -> imageResources[10]
            else -> imageResources[11]
        }
        if (animation) {
            val animation: ObjectAnimator =
                ObjectAnimator.ofFloat(drawableResource, View.ALPHA, 0.0f, 1.0f)
            animation.duration = 500
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(animation)
            animation.start()
        }
        drawableResource.setImageResource(animals[selectedImage])
    }

    fun hideImages() {
        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(p0: Long) {
                val timeLeft = p0 / 1000 + 1
                binding.countDownText.text = timeLeft.toString()
                //binding.mainButton.visibility = View.GONE
                // Avoid player can click over images when visible
                for (i in imageResources) {
                    i.isClickable = false
                }
            }

            override fun onFinish() {
                val resource = R.drawable.question_mark
                binding.invalidateAll()
                binding.countDownText.text = "0"
                for (i in 0..11) {
                    imageResources[i].setImageResource(resource)
                }
                // Image clickable back (for the user to actually be able to play)
                for (i in imageResources) {
                    i.isClickable = true
                }
            }
        }
        countDownTimer.start()
    }

    fun setListeners() {
        for (item in imageResources) {
            item.setOnClickListener {
                imageCounter++
                if (imageCounter <= 12) {
                    callMe(item)
                    if (imageCounter == 12) {
                        // Check if player got a highest score than the current one
                        if (score.currentScore.toInt() > score.highestScore.toInt()) {
                            binding.invalidateAll()
                            score.highestScore = score.currentScore
                        }
                        binding.mainButton.visibility = View.VISIBLE

                        // Reduce countDown in one second for each new round, 2000 (2 seconds) minimum
                        if (initialCountDown != 2000L)
                            initialCountDown -= 1000L
                    }
                }
            }
        }
    }

    private fun callMe(v: ImageView) {
        var image2Position: Int
        if (isFirstImage) {
            isFirstImage = !isFirstImage
            // Set the 'selected' drawable in the chosen imageView slot
            val image1Position = imagePosition(v)
            alreadySelected = image1Position
            // Find tapped1 in listNumbers and its couple (where the other image is)
            image2Position = listNumbers.indexOf(image1Position)
            if (image2Position % 2 == 0) {
                image2Position += 1
            } else {
                image2Position -= 1
            }
            animalLocation = selectedAnimal[(image2Position / 2) * 2 + 1]
            image2Position = listNumbers[image2Position]
            currentSecondImage = image2Position
            ////Log.v("testing", "Tapped image at: $image1Position. Couple image at: $image2Position")
            chooseImageLocation(image1Position, 13)
            //imageResources[image1Position - 1].isClickable = false
        } else {
            // An image was selected already, verify the chosen image now, is same as previous
            // If so, show the images of that animals
            // Else, fill both imageViews with 'incorrect' drawable
            isFirstImage = !isFirstImage
            val image1Position = imagePosition(v)
            // If user selects the same image that was already selected, make it unselected
            if(alreadySelected == image1Position){
                imageResources[image1Position-1].setImageResource(R.drawable.question_mark)
                imageCounter-=2
            }else
            if (image1Position == currentSecondImage) {
                // Correct choice, increment current score by 10 points
                updateScore(10)
                animation = true
                chooseImageLocation(image1Position, animalLocation - 1)
                chooseImageLocation(alreadySelected, animalLocation - 1)
                animation = false
                imageResources[alreadySelected - 1].isClickable = false
                imageResources[image1Position - 1].isClickable = false
            } else {
                // Game Over
                if ((binding.scoreText.text.toString().toInt() - 20) <= 0) {
                    gameOverDialog()
                    binding.invalidateAll()
                    score.currentScore = initialScore
                    val resource =
                        R.drawable.question_mark
                    for (i in 0..11) {
                        imageResources[i].setImageResource(resource)
                    }
                    for (i in imageResources) {
                        i.isClickable = false
                    }
                    initialCountDown = mainCounter
                    binding.mainButton.visibility = View.VISIBLE
                } else {
                    // Incorrect choice, decrease current score by 20 points
                    updateScore(-20)
                    chooseImageLocation(image1Position, 14)
                    chooseImageLocation(alreadySelected, 14)
                    imageResources[alreadySelected - 1].isClickable = false
                    imageResources[image1Position - 1].isClickable = false
                }
            }
            //Log.v("testing", "Animal at $animalLocation First animal is on $alreadySelected")
        }
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

    private fun imagePosition(view: ImageView): Int {
        return when (view.id) {
            R.id.image1 -> 1
            R.id.image2 -> 2
            R.id.image3 -> 3
            R.id.image4 -> 4
            R.id.image5 -> 5
            R.id.image6 -> 6
            R.id.image7 -> 7
            R.id.image8 -> 8
            R.id.image9 -> 9
            R.id.image10 -> 10
            R.id.image11 -> 11
            else -> 12
        }
    }
}