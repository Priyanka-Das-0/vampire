package com.example.beready.profile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import com.example.beready.R
import com.example.beready.homepage
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDomain: TextView
    private lateinit var tvEducation: TextView
    private lateinit var tvExperience: TextView
    private lateinit var tvSkills: TextView
    private lateinit var addMoreButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        addMoreButton = findViewById(R.id.addmore)

        tvName = findViewById(R.id.tv_name)
        tvEmail = findViewById(R.id.tv_email)
        tvDomain = findViewById(R.id.tv_domain)
        tvEducation = findViewById(R.id.tv_education)
        tvExperience = findViewById(R.id.tv_experience)
        tvSkills = findViewById(R.id.tv_skills)

        val user = auth.currentUser
        user?.let {
            val email = it.email?.replace(".", "_")

            database = FirebaseDatabase.getInstance().getReference("Users").child(email.toString())

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").value.toString()
                        val college = snapshot.child("clgName").value.toString()
                        val cgpa = snapshot.child("cgpa").value.toString()
                        val internship = snapshot.child("internship").value.toString()
                        val badges = snapshot.child("badge").children.map { it.value.toString() }
                        val skillsList = snapshot.child("skill").children.map { it.value.toString() }

                        tvName.text = "Name: $name"
                        tvEmail.text = "College: $college"
                        tvDomain.text = "CGPA: $cgpa"
                        tvEducation.text = "Internship: $internship"
                        tvExperience.text = "Badges: ${badges.joinToString(", ")}"
                        tvSkills.text = "Skills:\n*${skillsList.joinToString("\n*")}"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read errors
                }
            })
        }

        // AddMore button to open `res` activity for adding more data
        addMoreButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, res::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back -> {
                startActivity(Intent(this@ProfileActivity, homepage::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
