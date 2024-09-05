package com.example.beready

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.beready.community.AllMethods
import com.example.beready.community.Message
import com.google.firebase.database.DatabaseReference

class MessageAdapter(
    private val context: Context,
    private val messages: List<Message?>,
    private val messageDb: DatabaseReference
) : RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false)
        return MessageAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageAdapterViewHolder, position: Int) {
        val message = messages[position]

        if (message?.name == AllMethods.name) {
            holder.tvTitle.text = "You: ${message?.message}"
            holder.tvTitle.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            holder.itemView.setBackgroundColor(Color.parseColor("#EF9E73"))
        } else {
            holder.tvTitle.text = "${message?.name}: ${message?.message}"
            holder.ibDelete.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessageAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val ibDelete: ImageButton = itemView.findViewById(R.id.ibDelete)

        init {
            ibDelete.setOnClickListener {
                messages[adapterPosition]?.key?.let { it1 -> messageDb.child(it1).removeValue() }
            }
        }
    }
}