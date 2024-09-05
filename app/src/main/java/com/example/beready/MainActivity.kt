package com.example.beready

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.beready.databinding.ActivityMainBinding
import com.example.beready.profile.profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var uploadButton: Button
    private val PICK_RESUME_REQUEST = 71
    private var fileUri: Uri? = null
    private val storageReference = FirebaseStorage.getInstance().reference
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    // Updated to reflect the changes in `profile` data class
    private var profileData = profile()
    private var edu = arrayOf("Highest Education", "PhD", "Post-Graduate", "Bachelor", "Diploma", "Higher Secondary")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uploadButton = findViewById(R.id.resume)
        auth = FirebaseAuth.getInstance()

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("Users")

        // Handle resume upload button
        uploadButton.setOnClickListener {
            selectFile()
        }

        // Handle 'next' button click
        binding.apply {
            next.setOnClickListener {
                // Ensure all input values are captured correctly
                profileData.name = binding.name.text.toString().trim()
                profileData.clgRoll = binding.exp.text.toString().trim().toIntOrNull() // University Roll Number
                profileData.clgName = binding.clgName.text.toString().trim()
                profileData.education = binding.edu.selectedItem.toString()

                // Check if fileUri (resume) is selected
                if (fileUri != null) {
                    uploadResumeAndSaveProfile()
                } else {
                    Toast.makeText(this@MainActivity, "Please select a resume to upload", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Setup spinners for domain and education
        setupSpinners()
    }

    private fun setupSpinners() {
        val eduAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, edu)
        eduAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.edu.adapter = eduAdapter
        binding.edu.onItemSelectedListener = this@MainActivity
    }

    private fun selectFile() {
        val intent = Intent().apply {
            type = "application/pdf"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Resume"), PICK_RESUME_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_RESUME_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileUri = data.data // Store the selected resume URI
            Toast.makeText(this, "Resume selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadResumeAndSaveProfile() {
        val fileName = UUID.randomUUID().toString()
        val resumeRef = storageReference.child("resumes/$fileName.pdf")

        // Upload the PDF to Firebase Storage
        resumeRef.putFile(fileUri!!)
            .addOnSuccessListener {
                resumeRef.downloadUrl.addOnSuccessListener { uri ->
                    profileData.resumeUrl = uri.toString()

                    // Get the currently logged-in user's email
                    val user = auth.currentUser
                    if (user != null) {
                        val userEmail = user.email
                        val emailKey = userEmail!!.replace(".", "_")

                        // Save Profile data in Firebase Realtime Database
                        database.child(emailKey).setValue(profileData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, SignIn::class.java))
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Failed to save profile: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Resume Upload Failed", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Handle item selection if needed
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle case where nothing is selected
    }
}
