package com.example.beready.quiz
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beready.databinding.ActivitySubCategoryBinding


class SubCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubCategoryBinding
    private lateinit var subCategoryListAdapter: SubCategoryListAdapter
    private lateinit var quizModel: QuizModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModel = intent.getParcelableExtra("QUIZ_MODEL") ?: return

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        subCategoryListAdapter = SubCategoryListAdapter(quizModel.subCategories) { subCategory ->
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("SUB_CATEGORY", subCategory)
                putExtra("QUIZ_TITLE", quizModel.title)
                putExtra("QUIZ_TIME", quizModel.time)
            }
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = subCategoryListAdapter
    }
}
