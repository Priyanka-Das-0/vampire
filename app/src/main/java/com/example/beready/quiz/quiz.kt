package com.example.beready.quiz
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beready.databinding.ActivityQuizzBinding
import com.example.beready.homepage
import com.google.android.material.badge.ExperimentalBadgeUtils

import com.google.firebase.database.FirebaseDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class quiz : AppCompatActivity() {
    lateinit var binding: ActivityQuizzBinding
    lateinit var quizModelList: MutableList<QuizModel>
    lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizzBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        getDataFromFirebase()
        val onBackPressed = object : OnBackPressedCallback(true) {
            @OptIn(ExperimentalBadgeUtils::class)
            override fun handleOnBackPressed() {
                // Optional: You might want to finish the current activity
                // so that it doesn't remain in the back stack
                finish()

                // Navigate to the desired activity
                startActivity(Intent(this@quiz, homepage::class.java))
            }
        }
    }

    private fun setupRecyclerView() {
        binding.progressBar.visibility = View.GONE
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        binding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataSnapshot = FirebaseDatabase.getInstance().getReference("quizzes").get().await()
                withContext(Dispatchers.Main) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            val quizModel = snapshot.getValue(QuizModel::class.java)
                            if (quizModel != null) {
                                quizModelList.add(quizModel)

                                Log.d("Firebase", "Quiz added: ${quizModel.title}")
                            }
                        }
                    }
                    setupRecyclerView()
                }
            } catch (e: Exception) {
                Log.e("Firebase", "Error fetching data", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

}
