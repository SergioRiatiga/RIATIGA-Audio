package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SellerDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seller_dashboard)

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

        // Gestionar productos
        findViewById<AppCompatButton>(R.id.btnSellerProducts).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProductManagementActivity::class.java
                )
            )
        }

        // Historial de pedidos
        findViewById<AppCompatButton>(R.id.btnSellerOrders).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    OrderHistoryActivity::class.java
                )
            )
        }

        // Perfil del vendedor
        findViewById<AppCompatButton>(R.id.btnSellerProfile).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SellerProfileActivity::class.java
                )
            )
        }

        // Cerrar sesión
        findViewById<AppCompatButton>(R.id.btnLogout).setOnClickListener {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }
}