package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

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

        val bottomNavigation =
            findViewById<BottomNavigationView>(
                R.id.bottomNavigationAdmin
            )

        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navAdminHome -> {
                    true
                }

                R.id.navAdminUsers -> {
                    startActivity(
                        Intent(
                            this,
                            UserManagementActivity::class.java
                        )
                    )
                    true
                }

                R.id.navAdminProducts -> {
                    startActivity(
                        Intent(
                            this,
                            ProductManagementActivity::class.java
                        )
                    )
                    true
                }

                R.id.navAdminSales -> {
                    startActivity(
                        Intent(
                            this,
                            SalesReportActivity::class.java
                        )
                    )
                    true
                }

                else -> false
            }
        }

        // Cerrar sesión
        findViewById<AppCompatButton>(
            R.id.btnLogout
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // Al regresar al panel se marca nuevamente Inicio
        findViewById<BottomNavigationView>(
            R.id.bottomNavigationAdmin
        ).selectedItemId = R.id.navAdminHome
    }
}