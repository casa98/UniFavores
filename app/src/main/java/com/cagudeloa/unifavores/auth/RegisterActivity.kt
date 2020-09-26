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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

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
                    // Create a collection whose ID is the userId (which is unique)
                    val uid = auth.currentUser!!.uid

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    // I'll save some data here to then, save in on db
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["uid"] = uid
                    hashMap["username"] = username
                    hashMap["image"] = ""
                    databaseReference.setValue(hashMap).addOnCompleteListener(this){result ->
                        if(result.isSuccessful){
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this, "User couldn't be created", Toast.LENGTH_LONG).show()
                        }
                    }

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