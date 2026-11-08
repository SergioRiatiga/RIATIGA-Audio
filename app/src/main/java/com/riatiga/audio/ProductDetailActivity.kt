package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProductDetailActivity : AppCompatActivity() {

    private var productoSeleccionado = "air_pro"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)

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

        val ivProductImage = findViewById<ImageView>(R.id.ivProductImage)
        val tvProductName = findViewById<TextView>(R.id.tvProductName)
        val tvProductPrice = findViewById<TextView>(R.id.tvProductPrice)
        val tvProductFeatures = findViewById<TextView>(R.id.tvProductFeatures)

        productoSeleccionado =
            intent.getStringExtra("producto") ?: "air_pro"

        // Mostrar información según el producto seleccionado
        when (productoSeleccionado) {

            "one" -> {
                ivProductImage.setImageResource(R.drawable.riatiga_one)
                ivProductImage.contentDescription = getString(R.string.one)

                tvProductName.text = getString(R.string.one)
                tvProductPrice.text = getString(R.string.one_price)
                tvProductFeatures.text = getString(R.string.one_features)
            }

            else -> {
                ivProductImage.setImageResource(R.drawable.air_pro)
                ivProductImage.contentDescription = getString(R.string.air_pro)

                tvProductName.text = getString(R.string.air_pro)
                tvProductPrice.text = getString(R.string.air_pro_price)
                tvProductFeatures.text = getString(R.string.product_features)
            }
        }

        // Agregar producto al carrito
        findViewById<AppCompatButton>(R.id.btnAddToCart).setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)

            intent.putExtra(
                "producto",
                productoSeleccionado
            )

            startActivity(intent)
        }

        // Regresar al catálogo
        findViewById<AppCompatButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}