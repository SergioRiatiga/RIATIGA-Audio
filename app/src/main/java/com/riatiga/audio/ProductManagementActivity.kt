package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProductManagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_management)

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

        val cardAirPro = findViewById<LinearLayout>(R.id.cardAirPro)
        val cardOne = findViewById<LinearLayout>(R.id.cardOne)

        // Crear nuevo producto
        findViewById<AppCompatButton>(R.id.btnNewProduct).setOnClickListener {

            val intent = Intent(
                this,
                ProductFormActivity::class.java
            )

            startActivity(intent)
        }

        // Editar RIATIGA Air Pro
        findViewById<AppCompatButton>(R.id.btnEditAirPro).setOnClickListener {

            val intent = Intent(
                this,
                ProductFormActivity::class.java
            )

            intent.putExtra("producto", "air_pro")

            startActivity(intent)
        }

        // Editar RIATIGA One
        findViewById<AppCompatButton>(R.id.btnEditOne).setOnClickListener {

            val intent = Intent(
                this,
                ProductFormActivity::class.java
            )

            intent.putExtra("producto", "one")

            startActivity(intent)
        }

        // Eliminar RIATIGA Air Pro
        findViewById<AppCompatButton>(R.id.btnDeleteAirPro).setOnClickListener {

            mostrarConfirmacionEliminar(
                getString(R.string.air_pro),
                cardAirPro
            )
        }

        // Eliminar RIATIGA One
        findViewById<AppCompatButton>(R.id.btnDeleteOne).setOnClickListener {

            mostrarConfirmacionEliminar(
                getString(R.string.one),
                cardOne
            )
        }

        // Volver
        findViewById<AppCompatButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun mostrarConfirmacionEliminar(
        nombreProducto: String,
        tarjetaProducto: View
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                getString(R.string.delete_product)
            )
            .setMessage(
                getString(
                    R.string.delete_product_confirmation,
                    nombreProducto
                )
            )
            .setNegativeButton(
                getString(R.string.cancel),
                null
            )
            .setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->

                tarjetaProducto.visibility = View.GONE
            }
            .show()
    }
}