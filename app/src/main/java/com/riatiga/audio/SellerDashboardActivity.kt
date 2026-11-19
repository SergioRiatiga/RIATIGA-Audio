package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Panel principal del vendedor.
 * Los indicadores se obtienen directamente desde Firestore.
 */
class SellerDashboardActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var tvActiveProductsValue: TextView
    private lateinit var tvRecentOrdersValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seller_dashboard)

        db = FirebaseFirestore.getInstance()

        tvActiveProductsValue =
            findViewById(R.id.tvActiveProductsValue)

        tvRecentOrdersValue =
            findViewById(R.id.tvRecentOrdersValue)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
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

                R.id.navSellerHome -> true

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

            FirebaseAuth.getInstance().signOut()

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

        findViewById<BottomNavigationView>(
            R.id.bottomNavigationSeller
        ).selectedItemId =
            R.id.navSellerHome

        /*
         * Cada vez que el vendedor vuelve al panel,
         * los valores se consultan nuevamente.
         */
        cargarIndicadores()
    }

    /**
     * Consulta el número real de productos y pedidos.
     */
    private fun cargarIndicadores() {

        db.collection("productos")
            .get()
            .addOnSuccessListener { resultado ->

                tvActiveProductsValue.text =
                    resultado.size().toString()
            }

        db.collection("pedidos")
            .get()
            .addOnSuccessListener { resultado ->

                tvRecentOrdersValue.text =
                    resultado.size().toString()
            }
    }
}