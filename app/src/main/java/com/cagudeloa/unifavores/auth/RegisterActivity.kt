package com.cagudeloa.unifavores.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cagudeloa.unifavores.MainActivity
import com.cagudeloa.unifavores.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        login_button.setOnClickListener {
            val username = usernameEdit.text.toString()
            val email = email_text.text.toString()
            val password1 = password_text.text.toString()
            val password2 = confirm_password_text.text.toString()
            // TODO Improve this validations
            if(password1 == password2)
                if(password1.length > 5)
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        if(username.length > 4)
                            registerUser(username, email, password1)
                        else
                            Toast.makeText(this, "Please, enter a longer name", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(this, "Please, enter a valid email", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "Please, enter a longer password", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show()
        }
    }

    private fun registerUser(username: String, email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){createUser ->
                if (createUser.isSuccessful){

                    // TODO Here I have to create a collection for this user, save some data like {uid} and {username}

                    Log.i("INFO", auth.uid.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }else{
                    Toast.makeText(this, "User couldn't be created", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun goToSignIn(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}