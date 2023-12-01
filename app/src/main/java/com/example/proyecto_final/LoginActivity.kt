package com.example.proyecto_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var aunta: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        aunta = Firebase.auth

        val registerText: TextView = findViewById(R.id.textView_register_now)
        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        val loginButton: Button = findViewById(R.id.button_login)

        loginButton.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        //lets get input from the user
        val email: EditText = findViewById(R.id.editText_email_login)
        val password: EditText = findViewById(R.id.editText_contraseña_login)

        //null checks inputs
        if (email.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val emailInput = email.text.toString()
        val passwordInput = password.text.toString()

        aunta.signInWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, navigate to the Main Activity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(
                        baseContext,
                        "Success",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    // If sign in fails,
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "Authentication failed. ${it.localizedMessage}",
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }
}