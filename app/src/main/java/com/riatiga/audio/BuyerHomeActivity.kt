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
 * Inicio del comprador.
 * Los datos de los productos destacados se obtienen de Firestore.
 */
class BuyerHomeActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buyer_home)

        db = FirebaseFirestore.getInstance()

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
                R.id.bottomNavigationBuyer
            )

        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navBuyerHome -> true

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
            R.id.bottomNavigationBuyer
        ).selectedItemId =
            R.id.navBuyerHome

        cargarProductosDestacados()
    }

    /**
     * Consulta los productos por su código interno.
     * Así el nombre puede modificarse libremente.
     */
    private fun cargarProductosDestacados() {

        db.collection("productos")
            .get()
            .addOnSuccessListener { resultado ->

                for (documento in resultado.documents) {

                    val codigo =
                        documento.getString("codigo")
                            ?: continue

                    val nombre =
                        documento.getString("nombre")
                            ?: "Producto"

                    val precio =
                        documento.getString("precio")
                            ?: "0"

                    val caracteristicas =
                        documento.getString("caracteristicas")
                            ?: ""

                    when (codigo) {

                        "air_pro" -> {

                            findViewById<TextView>(
                                R.id.tvAirProName
                            ).text = nombre

                            findViewById<TextView>(
                                R.id.tvAirProPrice
                            ).text =
                                formatearPrecio(precio)

                            findViewById<TextView>(
                                R.id.tvAirProFeatures
                            ).text =
                                caracteristicas
                        }

                        "one" -> {

                            findViewById<TextView>(
                                R.id.tvOneName
                            ).text = nombre

                            findViewById<TextView>(
                                R.id.tvOnePrice
                            ).text =
                                formatearPrecio(precio)

                            findViewById<TextView>(
                                R.id.tvOneFeatures
                            ).text =
                                caracteristicas
                        }
                    }
                }
            }
    }

    private fun formatearPrecio(
        precio: String
    ): String {

        val numero =
            precio.replace(
                Regex("[^0-9]"),
                ""
            ).toLongOrNull()

        return if (numero != null) {

            "$" +
                    "%,d"
                        .format(numero)
                        .replace(",", ".")

        } else {

            "$$precio"
        }
    }
}