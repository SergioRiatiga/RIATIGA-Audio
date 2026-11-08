package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class SellerDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seller_dashboard)

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
                R.id.bottomNavigationSeller
            )

        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navSellerHome -> {
                    true
                }

                R.id.navSellerProducts -> {
                    startActivity(
                        Intent(
                            this,
                            ProductManagementActivity::class.java
                        )
                    )
                    true
                }

                R.id.navSellerOrders -> {
                    startActivity(
                        Intent(
                            this,
                            OrderHistoryActivity::class.java
                        )
                    )
                    true
                }

                R.id.navSellerProfile -> {
                    startActivity(
                        Intent(
                            this,
                            SellerProfileActivity::class.java
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
            R.id.bottomNavigationSeller
        ).selectedItemId = R.id.navSellerHome
    }
}