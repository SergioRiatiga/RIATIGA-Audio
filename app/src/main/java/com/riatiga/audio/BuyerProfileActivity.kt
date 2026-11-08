package com.riatiga.audio

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BuyerProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buyer_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val etName = findViewById<EditText>(R.id.etProfileName)
        val etEmail = findViewById<EditText>(R.id.etProfileEmail)
        val etPhone = findViewById<EditText>(R.id.etProfilePhone)
        val etAddress = findViewById<EditText>(R.id.etProfileAddress)

        // Datos de demostración del comprador
        etName.setText(
            getString(R.string.buyer_demo_name)
        )

        etEmail.setText(
            getString(R.string.buyer_demo_email)
        )

        etPhone.setText(
            getString(R.string.buyer_demo_phone)
        )

        etAddress.setText(
            getString(R.string.demo_location)
        )

        // Simulación de guardado del perfil
        findViewById<AppCompatButton>(R.id.btnSaveProfile).setOnClickListener {

            if (
                etName.text.toString().trim().isEmpty() ||
                etEmail.text.toString().trim().isEmpty()
            ) {

                Toast.makeText(
                    this,
                    getString(R.string.required_name_email),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            Toast.makeText(
                this,
                getString(R.string.changes_saved),
                Toast.LENGTH_SHORT
            ).show()
        }

        // Volver al inicio del comprador
        findViewById<AppCompatButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}