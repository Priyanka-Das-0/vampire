package com.example.beready.community

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beready.MessageAdapter
import com.example.beready.R
import com.example.beready.community.Message
import com.example.beready.homepage
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class Com : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var messageDb: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var user: User
    private lateinit var messages: MutableList<Message>
    private lateinit var rvMessage: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var imgButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.beready.R.layout.com)

        // Enable the back button in the toolbar
       // supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeComponents()

        // Handle the back button using the OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    private fun initializeComponents() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        user = User()
        messages = mutableListOf()

        rvMessage = findViewById(com.example.beready.R.id.rvMessage)
        etMessage = findViewById(com.example.beready.R.id.etMessage)
        imgButton = findViewById(com.example.beready.R.id.btnSend)

        imgButton.setOnClickListener(this)

        // Initialize the RecyclerView and Adapter
        rvMessage.layoutManager = LinearLayoutManager(this@Com)
        messageAdapter = MessageAdapter(this@Com, messages, database.getReference("messages"))
        rvMessage.adapter = messageAdapter
    }

    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = auth.currentUser
        currentUser?.let {
            user.uid = it.uid
            user.email = it.email

            database.getReference("Users").child(it.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData = snapshot.getValue(User::class.java)
                        userData?.let { user ->
                            this@Com.user = user
                            AllMethods.name = user.name
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error if needed
                    }
                })
        }

        messageDb = database.getReference("messages")
        messageDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    it.key = snapshot.key
                    messages.add(it)
                    messageAdapter.notifyItemInserted(messages.size - 1)
                    rvMessage.scrollToPosition(messages.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    it.key = snapshot.key
                    val index = messages.indexOfFirst { m -> m.key == it.key }
                    if (index != -1) {
                        messages[index] = it
                        messageAdapter.notifyItemChanged(index)
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    it.key = snapshot.key
                    val index = messages.indexOfFirst { m -> m.key == it.key }
                    if (index != -1) {
                        messages.removeAt(index)
                        messageAdapter.notifyItemRemoved(index)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle if needed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    override fun onClick(view: View) {
        if (!etMessage.text.toString().isEmpty()) {
            val message = Message(etMessage.text.toString(), user.name, user.uid)
            etMessage.setText("")
            messageDb.push().setValue(message)
        } else {
            Toast.makeText(applicationContext, "You cannot send a blank message", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the back button press in the toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.example.beready.R.menu.menu, menu)
        return true
    }

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
          R.id.back -> {
                startActivity(Intent(this@Com, homepage::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
