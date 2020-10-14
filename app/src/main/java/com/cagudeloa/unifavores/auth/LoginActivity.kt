package com.cagudeloa.unifavores.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cagudeloa.unifavores.MainActivity
import com.cagudeloa.unifavores.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val auth = FirebaseAuth.getInstance()
        val email = email_text.text.toString()
        val password = password_text.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        // Open home activity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Couldn't login\n${it.exception}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
        } else {
            Toast.makeText(this, "Both fields are required", Toast.LENGTH_LONG).show()
        }
    }

    fun goToSignUp(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}