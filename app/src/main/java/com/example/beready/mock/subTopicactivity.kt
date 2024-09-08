package com.example.beready.mock

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beready.databinding.ActivitySubtopicBinding
import subTopicAdapter


class subTopicActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubtopicBinding
    private lateinit var subCategoryListAdapter: subTopicAdapter
    private lateinit var quizModel: mockModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubtopicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModel = intent.getParcelableExtra("QUIZ_MODEL") ?: return

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        subCategoryListAdapter = subTopicAdapter(quizModel.subCategories) { subCategory ->
            val intent = Intent(this,mockActivity::class.java).apply {
                putExtra("SUB_CATEGORY", subCategory)
                putExtra("QUIZ_TITLE", quizModel.title)
                putExtra("QUIZ_TIME", quizModel.time)

            }
            startActivity(intent)
        }
        binding.mrecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mrecyclerView.adapter = subCategoryListAdapter
    }
}
