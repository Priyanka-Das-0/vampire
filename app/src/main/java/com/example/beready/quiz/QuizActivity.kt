package com.example.beready.quiz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.beready.R
import com.example.beready.databinding.ActivityQuizBinding
import com.example.beready.databinding.ScoreDialogBinding
import com.example.beready.profile.profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class QuizActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var subCategory: SubCategoryModel
    private lateinit var quizTitle: String
    private var quizTime: Int = 0
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var currentQuestionIndex = 0
    private var selectedAnswer = ""
    private var score = 0
    lateinit var imageView: ImageView
    var Profile = profile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the SubCategoryModel from the Intent
        subCategory = intent.getParcelableExtra("SUB_CATEGORY") ?: return
        quizTitle = intent.getStringExtra("QUIZ_TITLE") ?: ""
        quizTime = intent.getIntExtra("QUIZ_TIME", 0)
        auth = FirebaseAuth.getInstance()

        // Set up button listeners
        binding.apply {
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)
            nextBtn.setOnClickListener(this@QuizActivity)
        }

        loadQuestions()
        startTimer()
    }

    private fun startTimer() {
        val totalTimeInMillis = quizTime * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text = String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {
                finishQuiz()
            }
        }.start()
    }

    private fun loadQuestions() {
        selectedAnswer = ""
        if (currentQuestionIndex == subCategory.questions.size) {
            finishQuiz()
            return
        }

        binding.apply {
            questionIndicatorTextview.text = "Question ${currentQuestionIndex + 1}/ ${subCategory.questions.size}"
            questionProgressIndicator.progress =
                (currentQuestionIndex.toFloat() / subCategory.questions.size.toFloat() * 100).toInt()
            val question = subCategory.questions[currentQuestionIndex]
            questionTextview.text = question.question
            btn0.text = question.options[0]
            btn1.text = question.options[1]
            btn2.text = question.options[2]
            btn3.text = question.options[3]
        }
    }

    override fun onClick(view: View?) {
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.gray))
            btn1.setBackgroundColor(getColor(R.color.gray))
            btn2.setBackgroundColor(getColor(R.color.gray))
            btn3.setBackgroundColor(getColor(R.color.gray))
        }

        val clickedBtn = view as Button
        if (clickedBtn.id == R.id.next_btn) {
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(applicationContext, "Please select an answer to continue", Toast.LENGTH_SHORT).show()
                return
            }
            if (selectedAnswer == subCategory.questions[currentQuestionIndex].correct) {
                score++
                Log.i("Quiz Score", "Current score: $score")
            }
            currentQuestionIndex++
            loadQuestions()
        } else {
            selectedAnswer = clickedBtn.text.toString()
            clickedBtn.setBackgroundColor(getColor(R.color.orange))
        }
    }

    private fun finishQuiz() {
        val totalQuestions = subCategory.questions.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "$percentage %"

            if (percentage > 60) {
                val user = auth.currentUser
                if (user != null) {
                    val userEmail = user.email
                    val emailKey = userEmail!!.replace(".", "_")

                    // Determine badge to add
                    val badgeToAdd = when(subCategory.setTitle) {
                        "Set1" -> "Silver Badge"
                        "Set2" -> "Gold Badge"
                        else -> "Platinum Badge"
                    }

                    // Reference to Firebase
                    database = FirebaseDatabase.getInstance().getReference("Users")
                    database.child(emailKey).child("badges").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val currentBadges = mutableListOf<String>()

                            // Retrieve existing badges
                            for (badgeSnapshot in snapshot.children) {
                                val badge = badgeSnapshot.getValue(String::class.java)
                                if (badge != null) {
                                    currentBadges.add(badge)
                                }
                            }

                            // Add new badge if it doesn't exist
                            if (!currentBadges.contains(badgeToAdd)) {
                                currentBadges.add(badgeToAdd)

                                // Update badges in Firebase
                                database.child(emailKey).child("badges").setValue(currentBadges)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@QuizActivity,
                                            "Badge updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this@QuizActivity,
                                            "Failed to update badge",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@QuizActivity,
                                "Error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }

                // Display badge in the dialog
                when (subCategory.setTitle) {
                    "Set1" -> dialogBinding.badge.setImageResource(R.drawable.silver)
                    "Set2" -> dialogBinding.badge.setImageResource(R.drawable.gold)
                    else -> dialogBinding.badge.setImageResource(R.drawable.platinum)
                }
                scoreTitle.text = "Congrats! You have passed"
                scoreTitle.setTextColor(Color.BLUE)
            } else {
                scoreTitle.text = "Oops! You have failed"
                scoreTitle.setTextColor(Color.RED)
            }

            scoreSubtitle.text = "$score out of $totalQuestions are correct"
            finishBtn.setOnClickListener {
                finish()
            }
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()
    }
}
