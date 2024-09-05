package com.example.beready

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.example.beready.profile.res

import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

@ExperimentalBadgeUtils class homepage: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)


                    auth = FirebaseAuth.getInstance()
                        // Access the menu item by ID
                        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
                        val exploreMenuItem = navigationView.menu.findItem(R.id.jobrec)

                        // Create a BadgeDrawable
                        val badgeDrawable = BadgeDrawable.create(this)
                        badgeDrawable.number = 1 // Set the badge number as needed

                        // Set the badge to the menu item on the right side
                        (exploreMenuItem.icon as? ImageView)?.let {
                            BadgeUtils.attachBadgeDrawable(
                                badgeDrawable,
                                it,navigationView
                            )
                        }

                        // Access the toolbar and drawer layout
                        val toolbar = findViewById<Toolbar>(R.id.toolBar)
                        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

                        // to open navigation on click menu button
                        toolbar.setNavigationOnClickListener {
                            drawerLayout.open()
                        }

                        drawerLayout.setScrimColor(getColor(R.color.navigation_color))

                        // set on click listener for navigation menu items
                        navigationView.setNavigationItemSelectedListener {
                            // Close navigation drawer
                            drawerLayout.close()

                            // Mark the selected item as checked
                            it.isChecked = true

                            // Handle click events on menu items
                            when (it.itemId) {
                                R.id.Profile -> {
                                    val intent=Intent(this@homepage,com.example.beready.profile.ProfileActivity::class.java)
                                    // Handle setting menu click
                                    startActivity(intent)
                                    finish()
                                }

                                R.id.jobrec -> {
                                    Toast.makeText(this, "Hello I am Explore", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                R.id.resume -> {
                                    val intent=Intent(this@homepage,Resume::class.java)
                                    // Handle setting menu click
                                    startActivity(intent)
                                    finish()
                                }

                                R.id.test -> {
                                    val intent=Intent(this@homepage,com.example.beready.quiz.quiz::class.java)
                                    // Handle setting menu click
                                    startActivity(intent)
                                    finish()
                                }

                                R.id.community-> {
                                    val intent=Intent(this@homepage,com.example.beready.community.Com::class.java)
                                    // Handle setting menu click
                                    startActivity(intent)
                                    finish()
                                }

                               R.id.logout -> {
                                   auth.signOut()
                                   finish()
                                   startActivity(Intent(this@homepage,SignIn::class.java))
                                }


                            }

                            true
                        }

                        // Access the header view in navigation view

        val headerView = navigationView.getHeaderView(0)






    }
}
