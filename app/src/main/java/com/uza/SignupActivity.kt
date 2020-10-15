package com.uza

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        // Register a new user
        buttonSignin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        buttonSignup.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        if (userEmail.text.toString().isEmpty()) {
            userEmail.error = getString(R.string.emailEmptyError)
            userEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail.text.toString()).matches()) {
            userEmail.error = getString(R.string.emailInvalidError)
            userEmail.requestFocus()
            return
        }

        if (userPassword.text.toString().isEmpty()) {
            userPassword.error = getString(R.string.passwordEmptyError)
            userPassword.requestFocus()
            return
        }

        buttonSignup.setText(getString(R.string.signing_up))

        auth.createUserWithEmailAndPassword(userEmail.text.toString(), userPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user!!.sendEmailVerification()
                        .addOnCompleteListener { sendEmail ->
                            if (sendEmail.isSuccessful) {
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                        }
                } else {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.registrationFailedMessage),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}