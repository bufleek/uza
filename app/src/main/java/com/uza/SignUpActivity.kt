package com.uza

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.error_dialog.view.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: AlertDialog

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

        //set up loading dialog
        val builder = AlertDialog.Builder(this)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.loading_dialog, viewGroup, false)
        builder.setView(dialogView)
        loadingDialog = builder.create()
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

        loadingDialog.show()

        auth.createUserWithEmailAndPassword(userEmail.text.toString(), userPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                loadingDialog.dismiss()
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
                    showErrorDialog(task.exception?.message)
                }
            }
    }

    private fun showErrorDialog(message: String?) {
        if (message != null) {
            val builder = AlertDialog.Builder(this)
            val viewGroup: ViewGroup = findViewById(android.R.id.content)
            val dialogView: View =
                LayoutInflater.from(this).inflate(R.layout.error_dialog, viewGroup, false)
            builder.setView(dialogView)
            val dialog: AlertDialog = builder.create()
            dialogView.dialogMessage.text = message
            dialogView.btnDismissDialog.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}