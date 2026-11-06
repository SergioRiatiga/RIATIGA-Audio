package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CheckoutActivity : AppCompatActivity() {

    private var productoSeleccionado = "air_pro"
    private var cantidad = 1
    private var total = 699900

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)

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

        val tvProduct = findViewById<TextView>(R.id.tvCheckoutProduct)
        val tvQuantity = findViewById<TextView>(R.id.tvCheckoutQuantity)
        val tvTotal = findViewById<TextView>(R.id.tvCheckoutTotal)
        val etAddress = findViewById<EditText>(R.id.etDeliveryAddress)
        val btnPay = findViewById<AppCompatButton>(R.id.btnPay)

        // Recibir información enviada desde el carrito
        productoSeleccionado =
            intent.getStringExtra("producto") ?: "air_pro"

        cantidad =
            intent.getIntExtra("cantidad", 1)

        total =
            intent.getIntExtra("total", 699900)

        // Mostrar producto
        when (productoSeleccionado) {

            "one" -> {
                tvProduct.text = getString(R.string.one)
            }

            else -> {
                tvProduct.text = getString(R.string.air_pro)
            }
        }

        // Mostrar cantidad
        tvQuantity.text = "Cantidad: $cantidad"

        // Mostrar total
        val totalFormateado = String.format(
            "$%,d",
            total
        ).replace(",", ".")

        tvTotal.text = totalFormateado
        btnPay.text = "Pagar $totalFormateado"

        // Uso de ubicación para demostración del frontend
        findViewById<AppCompatButton>(R.id.btnUseLocation).setOnClickListener {

            etAddress.setText("Bogotá, Colombia")

            Toast.makeText(
                this,
                "Ubicación de demostración seleccionada",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Simulación del pago
        btnPay.setOnClickListener {

            val direccion = etAddress.text.toString().trim()

            if (direccion.isEmpty()) {

                Toast.makeText(
                    this,
                    "Ingresa una dirección de entrega",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Pago realizado")
                .setMessage(
                    "La compra fue procesada correctamente.\n\n" +
                            "Total: $totalFormateado"
                )
                .setPositiveButton("Aceptar") { _, _ ->

                    val intent = Intent(
                        this,
                        BuyerHomeActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP

                    startActivity(intent)
                    finish()
                }
                .show()
        }

        // Volver al carrito
        findViewById<AppCompatButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}