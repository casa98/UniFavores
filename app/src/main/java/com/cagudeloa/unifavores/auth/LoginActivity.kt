package com.cagudeloa.unifavores.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.cagudeloa.unifavores.MainActivity
import com.cagudeloa.unifavores.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        if(auth.uid != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            setContentView(R.layout.activity_login)

            login_button.setOnClickListener {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        val email = email_text.text.toString()
        val password = password_text.text.toString()
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){
                    if(it.isSuccessful){
                        // Open home activity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
        }else{
            Toast.makeText(this, "Both fields are required", Toast.LENGTH_LONG).show()
        }
    }

    fun goToSignUp(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}