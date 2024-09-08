package com.example.beready.resume

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import com.example.beready.R
import com.example.beready.homepage
import com.example.beready.profile.ProfileData
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Resume : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var btnGeneratePdf: Button

    // StateFlow to hold profile data
    private val _profileDataFlow = MutableStateFlow(ProfileData())
    val profileDataFlow: StateFlow<ProfileData> get() = _profileDataFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_pdf)

        val resumeCanvas = ResumeCanvas(this, profileDataFlow)
        val container = findViewById<FrameLayout>(R.id.resumeCanvasContainer)
        container.addView(resumeCanvas)

        btnGeneratePdf = findViewById(R.id.btnGeneratePdf)
        btnGeneratePdf.setOnClickListener {
            resumeCanvas.createPdf()
        }

        fetchProfileData()
        val onBackPressed = object : OnBackPressedCallback(true) {
            @OptIn(ExperimentalBadgeUtils::class)
            override fun handleOnBackPressed() {
                // Optional: You might want to finish the current activity
                // so that it doesn't remain in the back stack
                finish()

                // Navigate to the desired activity
                startActivity(Intent(this@Resume, homepage::class.java))
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressed)
    }

    private fun fetchProfileData() {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        user?.let {
            val emailKey = it.email!!.replace(".", "_")
            database = FirebaseDatabase.getInstance().getReference("Users").child(emailKey)

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val profileData = ProfileData(
                            name = snapshot.child("name").value.toString(),
                            email = emailKey,
                            clgName = snapshot.child("clgName").value.toString(),
                            cgpa = snapshot.child("cgpa").value.toString(),
                            year = snapshot.child("year").value.toString(),
                            education = snapshot.child("education").value.toString(),
                            internship = snapshot.child("internship").children.joinToString("\n") {"* ${it.value.toString()}" },
                            badge = snapshot.child("badge").children.joinToString("\n") { it.value.toString() },
                            skill = snapshot.child("skill").children.joinToString("\n") { "* ${it.value.toString()}" }
                        )

                        _profileDataFlow.value = profileData  // Update StateFlow
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

}
