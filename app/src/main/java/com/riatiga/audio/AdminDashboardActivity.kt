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
 * Panel principal del administrador.
 * Los indicadores se consultan directamente desde Firestore.
 */
class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var tvRegisteredUsersValue: TextView
    private lateinit var tvActiveProductsValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

        db = FirebaseFirestore.getInstance()

        tvRegisteredUsersValue =
            findViewById(R.id.tvRegisteredUsersValue)

        tvActiveProductsValue =
            findViewById(R.id.tvAdminActiveProductsValue)

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
                R.id.bottomNavigationAdmin
            )

        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navAdminHome -> true

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
            R.id.bottomNavigationAdmin
        ).selectedItemId =
            R.id.navAdminHome

        /*
         * Cada vez que el administrador regresa
         * al panel se actualizan los indicadores.
         */
        cargarIndicadores()
    }

    /**
     * Cuenta los usuarios y productos reales
     * almacenados actualmente en Firestore.
     */
    private fun cargarIndicadores() {

        db.collection("usuarios")
            .get()
            .addOnSuccessListener { resultado ->

                tvRegisteredUsersValue.text =
                    resultado.size().toString()
            }

        db.collection("productos")
            .get()
            .addOnSuccessListener { resultado ->

                tvActiveProductsValue.text =
                    resultado.size().toString()
            }
    }
}