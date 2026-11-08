package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class BuyerHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buyer_home)

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
                R.id.bottomNavigationBuyer
            )

        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navBuyerHome -> {
                    true
                }

                R.id.navBuyerCatalog -> {
                    startActivity(
                        Intent(
                            this,
                            BuyerCatalogActivity::class.java
                        )
                    )
                    true
                }

                R.id.navBuyerCart -> {
                    startActivity(
                        Intent(
                            this,
                            CartActivity::class.java
                        )
                    )
                    true
                }

                R.id.navBuyerProfile -> {
                    startActivity(
                        Intent(
                            this,
                            BuyerProfileActivity::class.java
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

        // Cada vez que se vuelve al inicio,
        // el menú marca nuevamente "Inicio"
        findViewById<BottomNavigationView>(
            R.id.bottomNavigationBuyer
        ).selectedItemId = R.id.navBuyerHome
    }
}