package com.example.beready

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignIn : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        firebaseAuth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("Users")

        // Initialize UI components
        emailField = findViewById(R.id.edt_em)
        passwordField = findViewById(R.id.edt_pass)
        loginButton = findViewById(R.id.btnSignin)

        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, homepage::class.java))
            finish() // Close SignIn activity if already signed in
        }

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, homepage::class.java)
                            intent.putExtra("EMAIL", email)
                            startActivity(intent)
                            finish()  // Close SignIn activity after successful login
                        } else {
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        val forget = findViewById<TextView>(R.id.forget)
        forget.setOnClickListener {
            showForgotPasswordDialog()
        }

        val notRegistered = findViewById<TextView>(R.id.notreg)
        notRegistered.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
    }

    private fun showForgotPasswordDialog() {
        val alert = AlertDialog.Builder(this@SignIn)
        val container = LinearLayout(this@SignIn).apply {
            orientation = LinearLayout.VERTICAL
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(50, 0, 50, 0)
        }

        val input = EditText(this@SignIn).apply {
            this.layoutParams = layoutParams
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setLines(1)
            maxLines = 1
        }

        container.addView(input, layoutParams)

        alert.setMessage("Enter your registered email")
        alert.setTitle("Forgot Password")
        alert.setView(container)

        alert.setPositiveButton("Submit") { dialogInterface, _ ->
            val enteredEmail = input.text.toString().trim()
            if (enteredEmail.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(enteredEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            dialogInterface.dismiss()
                            Toast.makeText(this@SignIn, "Email has been sent. Please check.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@SignIn, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this@SignIn, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        alert.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        alert.show()
    }


    @OptIn(ExperimentalBadgeUtils::class)
    override fun onStart() {
        super.onStart()
        if (intent.getStringExtra("key") == null) {
            firebaseAuth.currentUser?.let {
                startActivity(Intent(this, homepage::class.java))
                finish() // Close SignIn activity if already signed in
            }
        }
    }
}


