package com.example.beready

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.example.beready.community.Com
import com.example.beready.mock.mock
import com.example.beready.profile.ProfileActivity
import com.example.beready.resume.Resume
import com.example.beready.quiz.quiz
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class homepage: AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        auth = FirebaseAuth.getInstance()
        setupNavigationView()
        setupToolbarAndDrawer()
        updateCoinValue()
    }

    private fun setupNavigationView() {
        navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavigationMenuItemSelected(menuItem)
            true
        }
    }

    private fun setupToolbarAndDrawer() {
        val toolbar = findViewById<Toolbar>(R.id.toolBar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        toolbar.setNavigationOnClickListener {
            drawerLayout.open()
        }
        drawerLayout.setScrimColor(getColor(R.color.navigation_color))
    }

    private fun updateCoinValue() {
        val headerView = navigationView.getHeaderView(0)
        val coinTextView =findViewById<TextView>(R.id.coin)
        auth.currentUser?.let { user ->
            val emailKey = user.email?.replace(".", "_") ?: return
            database = FirebaseDatabase.getInstance().getReference("Users/$emailKey/coin")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val coins = snapshot.value.toString()
                        coinTextView.text = coins
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors more gracefully
                    coinTextView.text = "Error"
                }
            })
        }
    }

    private fun handleNavigationMenuItemSelected(menuItem: MenuItem): Boolean {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        drawerLayout.close()

        when (menuItem.itemId) {
            R.id.Profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.jobrec -> {
                startActivity(Intent(this, mock::class.java))
            }
            R.id.res -> {
                startActivity(Intent(this, Resume::class.java))
            }
            R.id.test -> {
                startActivity(Intent(this, quiz::class.java))
            }
            R.id.community -> {
                startActivity(Intent(this, Com::class.java))
            }
            R.id.logout -> {
                auth.signOut()
                startActivity(Intent(this, SignIn::class.java))
            }
        }
        return true
    }
}
