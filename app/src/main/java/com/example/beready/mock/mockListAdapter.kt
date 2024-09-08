package com.example.beready.mock


import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beready.databinding.QuizItemRecyclerRowBinding

class mockListAdapter(private val quizModelList: List<mockModel>) :
    RecyclerView.Adapter<mockListAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: QuizItemRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: mockModel) {
            binding.apply {
                quizTitleText.text = model.title
                quizTimeText.text = "${model.time} min"  // Correctly format the time
                root.setOnClickListener {
                    val intent = Intent(root.context, subTopicActivity::class.java).apply {
                        putExtra("QUIZ_MODEL", model)  // Pass the entire QuizModel to SubCategoryActivity
                    }
                    root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = QuizItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quizModelList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(quizModelList[position])
    }
}
