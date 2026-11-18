package com.riatiga.audio

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

/**
 * Pantalla para recuperar la contraseña mediante Firebase Authentication.
 */
class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recover_password)

        // Inicializa Firebase Authentication
        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val etRecoveryEmail =
            findViewById<EditText>(R.id.etRecoveryEmail)

        val btnSendInstructions =
            findViewById<AppCompatButton>(R.id.btnSendInstructions)

        // Envía el correo real de recuperación mediante Firebase
        btnSendInstructions.setOnClickListener {

            val email =
                etRecoveryEmail.text.toString().trim()

            if (email.isEmpty()) {

                Toast.makeText(
                    this,
                    "Ingresa tu correo electrónico",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        Toast.makeText(
                            this,
                            "Se enviaron las instrucciones a tu correo",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this,
                            "No fue posible enviar el correo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // Volver al Login
        findViewById<TextView>(R.id.tvBackToLogin)
            .setOnClickListener {
                finish()
            }
    }
}