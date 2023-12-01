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

class RegisterActivity : AppCompatActivity() {

        private lateinit var aunta: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        aunta = Firebase.auth

        val loginText: TextView = findViewById(R.id.editTextLoginNow)
        loginText.setOnClickListener {
          val intent = Intent(this,LoginActivity::class.java)
          startActivity(intent)
        }

        val registerButton: Button = findViewById(R.id.button_register)
        registerButton.setOnClickListener {
            //Conseguir email y contraseña del usuario
            formSignUp()
        }

    }

    private fun formSignUp() {
        val email = findViewById<EditText>(R.id.editText_email_register)
        val password = findViewById<EditText>(R.id.editText_contraseña_register)
        val passwordConfirmation = findViewById<EditText>(R.id.editText_contraseña_register_confirmacion)

        if (password.text.toString() != passwordConfirmation.text.toString()) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this, "Por favor llene todos los campos",Toast.LENGTH_SHORT).show()
            return
        }

        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()

        aunta.createUserWithEmailAndPassword(inputEmail,inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, let move to the next activity i.e MainActivity
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(
                        baseContext,
                        "Success",
                        Toast.LENGTH_SHORT).show()

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Error ocurred ${it.localizedMessage}",Toast.LENGTH_SHORT).show()
            }
    }
}