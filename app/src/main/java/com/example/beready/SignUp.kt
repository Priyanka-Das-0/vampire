package com.example.beready

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import com.example.beready.community.User
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("Users")

        val signupButton = findViewById<Button>(R.id.btnSignUp)
        signupButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.username).text.toString().trim()
            val email = findViewById<EditText>(R.id.edt_email).text.toString().trim()
            val password = findViewById<EditText>(R.id.edt_password).text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = auth.currentUser
                            val user = User(name, email, password)

                            firebaseUser?.let {
                                ref.child(it.uid)
                                    .setValue(user)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "User registered successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(this@SignUp,MainActivity::class.java)
                                            startActivity(intent)
                                            finish()  // Close the SignUp activity after successful registration
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Failed to save user data: ${task.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Registration failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
