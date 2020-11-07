package com.cagudeloa.unifavores.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cagudeloa.unifavores.UniFavoresMainActivity
import com.cagudeloa.unifavores.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        // User click on Register button
        login_button.setOnClickListener {
            val username = usernameEdit.text.toString()
            val email = email_text.text.toString()
            val password1 = password_text.text.toString()
            val password2 = confirm_password_text.text.toString()

            val answer = isValid(username, email, password1, password2)
            if (answer == null) {      // Valid form
                showAndHide(View.GONE)
                registerProgressBarLayout.visibility = View.VISIBLE
                viewModel.requestRegister(username, email, password1)
                viewModel.registerSuccess.observe(this, Observer { authResult ->
                    if (authResult == null) {     // Success
                        registerProgressBarLayout.visibility = View.GONE
                        navigateToHome()
                    } else {  // Error
                        registerProgressBarLayout.visibility = View.GONE
                        showAndHide(View.VISIBLE)
                        Toast.makeText(
                            this,
                            "User couldn't be created\n$authResult",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            } else {
                Toast.makeText(this, answer, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isValid(
        username: String,
        email: String,
        password1: String,
        password2: String
    ): String? {
        if (username.length < 5)
            return "Ingresa un nombre más largo"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Correo inválido"
        else if (password1.length < 5)
            return "Contraseña demasiado corta"
        else if (password1 != password2)
            return "las contraseñas no coinciden"
        return null
    }

    private fun navigateToHome() {
        val intent = Intent(this, UniFavoresMainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showAndHide(state: Int) {
        usernameEdit.visibility = state
        email_text.visibility = state
        password_text.visibility = state
        confirm_password_text.visibility = state
        login_button.visibility = state
        login_label.visibility = state
    }

    fun goToSignIn(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}