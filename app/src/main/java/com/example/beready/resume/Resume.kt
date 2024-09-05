package com.example.beready

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.beready.profile.ProfileActivity
import com.example.beready.utils.PdfUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Resume : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var btnGeneratePdf: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_pdf)

        auth = FirebaseAuth.getInstance()
        btnGeneratePdf = findViewById(R.id.btnGeneratePdf)

        btnGeneratePdf.setOnClickListener {
            generatePdf()
        }
    }

    private fun generatePdf() {
        val user = auth.currentUser
        user?.let {
            val emailKey = it.email!!.replace(".", "_")
            database = FirebaseDatabase.getInstance().getReference("Users").child(emailKey)

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val profileData = mutableMapOf<String, String>()
                        profileData["Name"] = snapshot.child("name").value.toString()
                        profileData["College"] = snapshot.child("clgName").value.toString()
                        profileData["CGPA"] = snapshot.child("cgpa").value.toString()
                        profileData["Internship"] = snapshot.child("internship").value.toString()
                        profileData["Badges"] = snapshot.child("badge").children.joinToString(", ") { it.value.toString() }
                        profileData["Skills"] = snapshot.child("skill").children.joinToString("\n") { "• ${it.value.toString()}" }

                        PdfUtils.createPdf(this@Resume, profileData)
                    } else {
                        PdfUtils.createPdf(this@Resume, emptyMap())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read errors
                }
            })
        }
    }
}
