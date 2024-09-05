package com.example.beready.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.beready.R
import com.example.beready.databinding.Res1Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class res : AppCompatActivity() {

    private val binding: Res1Binding by lazy {
        Res1Binding.inflate(layoutInflater)
    }

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var skillsContainer: EditText
    private lateinit var buttonAddSkill: ImageView
    private var skillCount = 0
    private val skills = mutableListOf<String>()

    private lateinit var intContainer: EditText
    private lateinit var buttonAddSkill1: ImageView
    private var intCount = 0
    private val internships = mutableListOf<String>()

    private lateinit var projectContainer: EditText
    private lateinit var plContainer: EditText
    private lateinit var buttonAddSkill2: ImageView
    private var projectCount = 0
    private val projects = mutableListOf<String>()
    private val pl = mutableListOf<String>()
    private var plCount = 0
    private var Profile = profile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            next1.setOnClickListener {
                Profile.cgpa = cgpa.text.toString().trim().toFloat()
                Profile.year = year.text.toString().trim().toInt()
            }
        }

        // Set up skills addition
        skillsContainer = findViewById(R.id.buttonSubmit)
        buttonAddSkill = findViewById(R.id.addBtn)
        buttonAddSkill.setOnClickListener { addSkill() }

        intContainer = findViewById(R.id.internship)
        buttonAddSkill1 = findViewById(R.id.addBtn1)
        buttonAddSkill1.setOnClickListener { addInternship() }

        projectContainer = findViewById(R.id.project)
        plContainer = findViewById(R.id.projectlink)
        buttonAddSkill2 = findViewById(R.id.addBtn2)
        buttonAddSkill2.setOnClickListener { addProject() }

        val user = auth.currentUser
        user?.let {
            val emailKey = it.email!!.replace(".", "_")

            // Save Profile in Firebase Realtime Database using updateChildren to avoid overwriting
            database = FirebaseDatabase.getInstance().getReference("Users")
            binding.next1.setOnClickListener {
                val updates = mapOf(
                    "cgpa" to Profile.cgpa,
                    "year" to Profile.year,
                    "skill" to Profile.skill,
                    "internship" to Profile.internship,
                    "projects" to Profile.projects,
                    "projectlink" to Profile.projectlink
                )
                database.child(emailKey).updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this@res, "Profile updated", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@res, ProfileActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun addSkill() {
        val skill = skillsContainer.text.toString().trim()
        if (skill.isNotEmpty()) {
            skills.add(skill)
            Profile.skill = skills
            skillCount++
            skillsContainer.text.clear()
            skillsContainer.hint = "Skill $skillCount"
            Toast.makeText(this, "Skill added: $skill", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter a skill", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addInternship() {
        val internship = intContainer.text.toString().trim()
        if (internship.isNotEmpty()) {
            internships.add(internship)
            Profile.internship = internships
            intCount++
            intContainer.text.clear()
            intContainer.hint = "Internship $intCount"
            Toast.makeText(this, "Internship added: $internship", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter an internship", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addProject() {
        val project = projectContainer.text.toString().trim()
        val projectLink = plContainer.text.toString().trim()
        if (project.isNotEmpty()) {
            projects.add(project)
            Profile.projects = projects
            projectCount++
            projectContainer.text.clear()
            projectContainer.hint = "Project $projectCount"
            Toast.makeText(this, "Project added: $project", Toast.LENGTH_SHORT).show()

            if (projectLink.isNotEmpty()) {
                pl.add(projectLink)
                Profile.projectlink = pl
                plCount++
                plContainer.text.clear()
                plContainer.hint = "Project Link $plCount"
                Toast.makeText(this, "Project Link added: $projectLink", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please enter a project", Toast.LENGTH_SHORT).show()
        }
    }
}
