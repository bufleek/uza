package com.uza.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.uza.DashboardActivity
import com.uza.LoginActivity
import com.uza.R

class SplashActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: created")
        auth = FirebaseAuth.getInstance()
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val isFirstLaunch: Boolean = preferences.getBoolean("isFirstLaunch", true)
        if (isFirstLaunch){
            //launch onboarding screen
            val editor = preferences.edit()
            editor.putBoolean("isFirstLaunch", false)
            editor.commit()

            //delete and launch onboarding screen
            val currentUser = auth.currentUser
            if (currentUser != null && currentUser.isEmailVerified){
                Log.d(TAG, "onCreate: dash starting")
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
            else
            {
                Log.d(TAG, "onCreate: login starting")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }else{
            val currentUser = auth.currentUser
            if (currentUser != null && currentUser.isEmailVerified){
                Log.d(TAG, "onCreate: dash starting")
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
            else
            {
                Log.d(TAG, "onCreate: login starting")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }


    }

    companion object {
        private const val TAG = "SplashActivity"
    }
}