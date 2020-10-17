package com.cagudeloa.unifavores.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cagudeloa.unifavores.MainActivity
import com.cagudeloa.unifavores.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        login_button.setOnClickListener {
            //loginUser()
            val email = email_text.text.toString()
            val password = password_text.text.toString()
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                showAndHide(View.GONE)
                loginProgressBarLayout.visibility = View.VISIBLE

                viewModel.requestLogin(email, password)
                viewModel.loginSuccess.observe(this, Observer { authResult ->
                    if (authResult == null) {     // Success
                        loginProgressBarLayout.visibility = View.GONE
                        navigateToHome()

                    } else {  // Error
                        loginProgressBarLayout.visibility = View.GONE
                        showAndHide(View.VISIBLE)
                        Toast.makeText(this, "Couldn't login\n$authResult", Toast.LENGTH_LONG)
                            .show()
                    }
                })
            } else {
                Toast.makeText(this, "Ambos campos son requeridos", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showAndHide(state: Int) {
        email_text.visibility = state
        password_text.visibility = state
        login_button.visibility = state
        login_label.visibility = state
    }

    fun goToSignUp(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}