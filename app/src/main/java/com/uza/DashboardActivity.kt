package com.uza

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uza.ui.chat.ChatMainActivity
import androidx.core.view.GravityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.uza.data.models.User
import com.uza.ui.post.CreatePostActivity
import kotlinx.android.synthetic.main.nav_header_main.view.*

class DashboardActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val auth = Firebase.auth
    private val database = Firebase.database
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_products,
//            R.id.nav_categories,
                R.id.nav_messages,
                R.id.nav_sell,
//            R.id.nav_request,
//            R.id.nav_settings
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_sell -> {
                    startActivity(Intent(this, CreatePostActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_messages -> {
                    startActivity(Intent(this, ChatMainActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_messages -> {
                    startActivity(Intent(this, ChatMainActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> true
            }

        }

        //check if user is in database
        //and
        //set nav header text
        val user = auth.currentUser
        if (user != null) {
            val navHeader = navView.getHeaderView(0)
            navHeader.currentUserEmail.text = user.email

            val userReference = database.getReference("users/${user.uid}")
            userReference.limitToLast(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.hasChildren()) {
                            val newUser = User()
                            newUser.email = user.email
                            newUser.name = user.displayName
                            userReference.setValue(newUser)
                        } else {
                            val existingUser = snapshot.getValue(User::class.java)
                            if (existingUser != null) {
                                navHeader.currentUser.text = existingUser.name?.capitalize()
                                if (existingUser.name == null) {
                                    userReference.child("name").setValue(user.displayName)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}

                })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        navView.setCheckedItem(R.id.nav_products)
        val currentUser = auth.currentUser
        if (currentUser == null) {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}