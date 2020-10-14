package com.uza

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Register a new user
        buttonSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        // Log in the user if credential matches
        buttonLogin.setOnClickListener {
            loginUser()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null ) {
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(baseContext, getString(R.string.verifyMail), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser() {
        if (userEmailLogin.text.toString().isEmpty()) {
            userEmailLogin.error = getString(R.string.emailEmptyError)
            userEmailLogin.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmailLogin.text.toString()).matches()) {
            userEmailLogin.error = getString(R.string.emailInvalidError)
            userEmailLogin.requestFocus()
            return
        }

        if (userPassword.text.toString().isEmpty()) {
            userPassword.error = getString(R.string.passwordEmptyError)
            userPassword.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(userEmailLogin.text.toString(), userPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }
}