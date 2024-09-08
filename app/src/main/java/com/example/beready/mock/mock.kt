package com.example.beready.mock
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beready.databinding.ActivityMockBinding
import com.example.beready.databinding.ActivityQuizzBinding
import com.example.beready.homepage
import com.google.android.material.badge.ExperimentalBadgeUtils

import com.google.firebase.database.FirebaseDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class mock : AppCompatActivity() {
    lateinit var binding: ActivityMockBinding
    lateinit var quizModelList: MutableList<mockModel>
    lateinit var adapter: mockListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMockBinding.inflate(layoutInflater)
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
                startActivity(Intent(this@mock, homepage::class.java))
            }
        }
    }

    private fun setupRecyclerView() {
        binding.mprogressBar.visibility = View.GONE
        adapter = mockListAdapter(quizModelList)
        binding.mrecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mrecyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        binding.mprogressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataSnapshot = FirebaseDatabase.getInstance().getReference("Mock/Mock").get().await()
                withContext(Dispatchers.Main) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            val quizModel = snapshot.getValue(mockModel::class.java)
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
                    binding.mprogressBar.visibility = View.GONE
                }
            }
        }
    }

}
