package com.riatiga.audio

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SellerProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seller_profile)

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

        val etName = findViewById<EditText>(R.id.etSellerName)
        val etEmail = findViewById<EditText>(R.id.etSellerEmail)
        val etPhone = findViewById<EditText>(R.id.etSellerPhone)
        val etStore = findViewById<EditText>(R.id.etSellerStore)

        // Datos de demostración del vendedor
        etName.setText(
            getString(R.string.seller_demo_name)
        )

        etEmail.setText(
            getString(R.string.seller_demo_email)
        )

        etPhone.setText(
            getString(R.string.seller_demo_phone)
        )

        etStore.setText(
            getString(R.string.seller_demo_store)
        )

        // Guardar cambios del perfil
        findViewById<AppCompatButton>(R.id.btnSaveSellerProfile).setOnClickListener {

            val nombre = etName.text.toString().trim()
            val correo = etEmail.text.toString().trim()
            val tienda = etStore.text.toString().trim()

            if (
                nombre.isEmpty() ||
                correo.isEmpty() ||
                tienda.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    getString(R.string.seller_required_fields),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            Toast.makeText(
                this,
                getString(R.string.seller_profile_updated),
                Toast.LENGTH_SHORT
            ).show()
        }

        // Volver al panel del vendedor
        findViewById<AppCompatButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}