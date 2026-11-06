package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BuyerHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buyer_home)

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

        // Abrir catálogo
        findViewById<AppCompatButton>(R.id.btnBuyerCatalog).setOnClickListener {
            startActivity(
                Intent(this, BuyerCatalogActivity::class.java)
            )
        }

        // Abrir carrito
        findViewById<AppCompatButton>(R.id.btnBuyerCart).setOnClickListener {
            startActivity(
                Intent(this, CartActivity::class.java)
            )
        }

        // Abrir perfil
        findViewById<AppCompatButton>(R.id.btnBuyerProfile).setOnClickListener {
            startActivity(
                Intent(this, BuyerProfileActivity::class.java)
            )
        }

        // Cerrar sesión
        findViewById<AppCompatButton>(R.id.btnLogout).setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }
}