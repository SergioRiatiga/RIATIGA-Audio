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
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Pantalla para finalizar la compra.
 * Verifica nuevamente el producto y su precio en Firestore.
 */
class CheckoutActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private var productId: String? = null
    private var codigoProducto = "air_pro"

    private var cantidad = 1
    private var total = 0L

    private lateinit var tvProduct: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnPay: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)

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

        tvProduct =
            findViewById(R.id.tvCheckoutProduct)

        tvQuantity =
            findViewById(R.id.tvCheckoutQuantity)

        tvTotal =
            findViewById(R.id.tvCheckoutTotal)

        val etAddress =
            findViewById<EditText>(
                R.id.etDeliveryAddress
            )

        btnPay =
            findViewById(R.id.btnPay)

        productId =
            intent.getStringExtra("productId")

        codigoProducto =
            intent.getStringExtra("codigo")
                ?: "air_pro"

        cantidad =
            intent.getIntExtra(
                "cantidad",
                1
            )

        total =
            intent.getLongExtra(
                "total",
                0L
            )

        tvQuantity.text =
            getString(
                R.string.quantity_value,
                cantidad
            )

        /*
         * Primero mostramos el valor recibido y
         * después Firestore lo confirma y actualiza.
         */
        actualizarTotal(total)

        cargarProductoActual()

        findViewById<AppCompatButton>(
            R.id.btnUseLocation
        ).setOnClickListener {

            etAddress.setText(
                getString(
                    R.string.demo_location
                )
            )

            Toast.makeText(
                this,
                getString(
                    R.string.demo_location_selected
                ),
                Toast.LENGTH_SHORT
            ).show()
        }

        btnPay.setOnClickListener {

            val direccion =
                etAddress.text
                    .toString()
                    .trim()

            if (direccion.isEmpty()) {

                Toast.makeText(
                    this,
                    getString(
                        R.string.enter_delivery_address
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val totalFormateado =
                formatearPrecio(total)

            AlertDialog.Builder(this)
                .setTitle(
                    getString(
                        R.string.payment_completed
                    )
                )
                .setMessage(
                    getString(
                        R.string.payment_success_message,
                        totalFormateado
                    )
                )
                .setPositiveButton(
                    getString(
                        R.string.accept
                    )
                ) { _, _ ->

                    val intent =
                        Intent(
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

        findViewById<AppCompatButton>(
            R.id.btnBack
        ).setOnClickListener {

            finish()
        }
    }

    /**
     * Confirma nombre y precio directamente contra Firestore.
     */
    private fun cargarProductoActual() {

        if (productId == null) {
            return
        }

        db.collection("productos")
            .document(productId!!)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    val nombre =
                        documento.getString("nombre")
                            ?: "Producto"

                    val precioTexto =
                        documento.getString("precio")
                            ?: "0"

                    val precio =
                        precioTexto.replace(
                            Regex("[^0-9]"),
                            ""
                        ).toLongOrNull()
                            ?: 0L

                    tvProduct.text =
                        nombre

                    /*
                     * El total se vuelve a calcular usando
                     * el precio actual de Firestore.
                     */
                    total =
                        precio * cantidad

                    actualizarTotal(total)
                }
            }
    }

    private fun actualizarTotal(
        nuevoTotal: Long
    ) {

        total =
            nuevoTotal

        val totalFormateado =
            formatearPrecio(total)

        tvTotal.text =
            totalFormateado

        btnPay.text =
            getString(
                R.string.pay_amount,
                totalFormateado
            )
    }

    private fun formatearPrecio(
        precio: Long
    ): String {

        return "$" +
                "%,d"
                    .format(precio)
                    .replace(",", ".")
    }
}