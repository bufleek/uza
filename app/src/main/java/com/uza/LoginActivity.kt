package com.uza

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.error_dialog.view.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Register a new user
        buttonSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Log in the user if credential matches
        buttonLogin.setOnClickListener {
            loginUser()
        }

        googleSignIn.setOnClickListener {
            googleSignIn()
        }

        //set up loading dialog
        val builder = AlertDialog.Builder(this)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.loading_dialog, viewGroup, false)
        builder.setView(dialogView)
        loadingDialog = builder.create()
    }

    private fun googleSignIn() {
        loadingDialog.show()
        val intent = mGoogleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            loadingDialog.dismiss()
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!

                updateUIWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(baseContext, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentUser = auth.currentUser
        updateUI(currentUser)

    }

    private fun updateUIWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT)
                        .show()
                    updateUI(user)
                } else {
                    Toast.makeText(baseContext, task.exception?.message, Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
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
        loadingDialog.show()

        auth.signInWithEmailAndPassword(
            userEmailLogin.text.toString(),
            userPassword.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                loadingDialog.dismiss()
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT)
                        .show()
                    updateUI(user)
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