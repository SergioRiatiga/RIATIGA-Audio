package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<AppCompatButton>(R.id.btnLogin)

        // Inicio de sesión según las credenciales y el rol
        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim().lowercase()
            val password = etPassword.text.toString()

            if (password != "1234") {
                mostrarCredencialesIncorrectas()
                return@setOnClickListener
            }

            when (email) {

                "comprador@riatiga.com" -> {
                    startActivity(
                        Intent(this, BuyerHomeActivity::class.java)
                    )
                }

                "vendedor@riatiga.com" -> {
                    startActivity(
                        Intent(this, SellerDashboardActivity::class.java)
                    )
                }

                "admin@riatiga.com" -> {
                    startActivity(
                        Intent(this, AdminDashboardActivity::class.java)
                    )
                }

                else -> {
                    mostrarCredencialesIncorrectas()
                }
            }
        }

        // Recuperación de contraseña
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            startActivity(
                Intent(this, RecoverPasswordActivity::class.java)
            )
        }
    }

    private fun mostrarCredencialesIncorrectas() {
        Toast.makeText(
            this,
            "Correo o contraseña incorrectos",
            Toast.LENGTH_SHORT
        ).show()
    }
}