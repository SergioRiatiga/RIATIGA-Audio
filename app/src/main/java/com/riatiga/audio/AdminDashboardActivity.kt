package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

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

        // Gestionar usuarios
        findViewById<AppCompatButton>(R.id.btnAdminUsers).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    UserManagementActivity::class.java
                )
            )
        }

        // Gestionar productos
        findViewById<AppCompatButton>(R.id.btnAdminProducts).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProductManagementActivity::class.java
                )
            )
        }

        // Reporte de ventas
        findViewById<AppCompatButton>(R.id.btnAdminSales).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SalesReportActivity::class.java
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