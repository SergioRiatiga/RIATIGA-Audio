package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CartActivity : AppCompatActivity() {

    private var cantidad = 1
    private var precioUnitario = 699900
    private var productoSeleccionado = "air_pro"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

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

        val tvProductName = findViewById<TextView>(R.id.tvCartProductName)
        val tvProductPrice = findViewById<TextView>(R.id.tvCartProductPrice)
        val tvQuantity = findViewById<TextView>(R.id.tvQuantity)
        val tvTotal = findViewById<TextView>(R.id.tvCartTotal)

        productoSeleccionado =
            intent.getStringExtra("producto") ?: "air_pro"

        when (productoSeleccionado) {

            "one" -> {
                tvProductName.text = getString(R.string.one)
                tvProductPrice.text = getString(R.string.one_price)
                precioUnitario = 849900
            }

            else -> {
                tvProductName.text = getString(R.string.air_pro)
                tvProductPrice.text = getString(R.string.air_pro_price)
                precioUnitario = 699900
            }
        }

        actualizarTotal(tvQuantity, tvTotal)

        // Disminuir cantidad
        findViewById<AppCompatButton>(R.id.btnDecrease).setOnClickListener {
            if (cantidad > 1) {
                cantidad--
                actualizarTotal(tvQuantity, tvTotal)
            }
        }

        // Aumentar cantidad
        findViewById<AppCompatButton>(R.id.btnIncrease).setOnClickListener {
            cantidad++
            actualizarTotal(tvQuantity, tvTotal)
        }

        // Continuar al pago
        findViewById<AppCompatButton>(R.id.btnCheckout).setOnClickListener {

            val intent = Intent(this, CheckoutActivity::class.java)

            intent.putExtra("producto", productoSeleccionado)
            intent.putExtra("cantidad", cantidad)
            intent.putExtra("total", precioUnitario * cantidad)

            startActivity(intent)
        }

        // Volver
        findViewById<AppCompatButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun actualizarTotal(
        tvQuantity: TextView,
        tvTotal: TextView
    ) {
        tvQuantity.text = cantidad.toString()

        val total = precioUnitario * cantidad

        tvTotal.text = String.format(
            "$%,d",
            total
        ).replace(",", ".")
    }
}