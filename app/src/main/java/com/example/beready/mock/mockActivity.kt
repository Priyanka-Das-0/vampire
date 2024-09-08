package com.example.beready.mock

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.beready.R
import com.example.beready.databinding.ActivityQuizBinding
import com.example.beready.databinding.ScoreDialogBinding
import com.example.beready.homepage
import com.example.beready.profile.profile
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class mockActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var subCategory: subTopic
    private lateinit var quizTitle: String
    private var quizTime: Int = 0
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var currentQuestionIndex = 0
    private var selectedAnswer = ""
    private var score = 0
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
            btn0.setOnClickListener(this@mockActivity)
            btn1.setOnClickListener(this@mockActivity)
            btn2.setOnClickListener(this@mockActivity)
            btn3.setOnClickListener(this@mockActivity)
            nextBtn.setOnClickListener(this@mockActivity)
        }

        loadQuestions()
        startTimer()
        val onBackPressed = object : OnBackPressedCallback(true) {
            @OptIn(ExperimentalBadgeUtils::class)
            override fun handleOnBackPressed() {
                // Optional: You might want to finish the current activity
                // so that it doesn't remain in the back stack
                finish()

                // Navigate to the desired activity
                startActivity(Intent(this@mockActivity, homepage::class.java))
            }
        }
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
                user?.let {
                    val userEmail = user.email!!.replace(".", "_")
                    database = FirebaseDatabase.getInstance().getReference("Users")
                    database.child(userEmail).child("coin").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val coin = snapshot.getValue(Int::class.java) ?: 0
                            database.child(userEmail).child("coin").setValue(coin + 10)
                                .addOnSuccessListener {
                                    // Coin update success
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@mockActivity, "Failed to update coin", Toast.LENGTH_SHORT).show()
                                }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("FirebaseError", "Error: ${error.message}")
                        }
                    })
                }
                scoreTitle.text = "Congrats! You have passed"
                scoreTitle.setTextColor(Color.BLUE)
            } else {
                scoreTitle.text = "Oops! You have failed"
                scoreTitle.setTextColor(Color.RED)
                doc.text = "Click here"

                // Fetch the documentation link by iterating through subCategories
                database = FirebaseDatabase.getInstance().getReference("Mock/Mock")

                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { mockChildSnapshot ->
                            mockChildSnapshot.child("subCategories").children.forEach { subCategorySnapshot ->
                                val subCat = subCategorySnapshot.getValue(subTopic::class.java)
                                if (subCat?.setTitle == subCategory.setTitle) {
                                    doc.setOnClickListener {
                                        openDocumentationLink(subCat.documentationLink)
                                    }
                                    return
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseError", "Error: ${error.message}")
                    }
                })
            }
            scoreSubtitle.text = "$score out of $totalQuestions are correct"
            finishBtn.setOnClickListener {
                finish()
            }
        }

        AlertDialog.Builder(this@mockActivity)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()
    }

    private fun openDocumentationLink(url: String?) {
        Log.d("DocumentationLink", "Opening URL: $url")  // Log URL for debugging
        if (url.isNullOrEmpty()) {
            Toast.makeText(this, "No documentation link available.", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}
