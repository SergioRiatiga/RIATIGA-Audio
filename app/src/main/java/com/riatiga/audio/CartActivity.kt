package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Carrito de compra.
 * El producto se consulta mediante su ID de Firestore.
 */
class CartActivity : AppCompatActivity() {

    private var cantidad = 1
    private var precioUnitario = 0L

    private var productId: String? = null
    private var codigoProducto = "air_pro"

    private lateinit var db: FirebaseFirestore

    private lateinit var tvProductName: TextView
    private lateinit var tvProductPrice: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

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

        tvProductName =
            findViewById(R.id.tvCartProductName)

        tvProductPrice =
            findViewById(R.id.tvCartProductPrice)

        tvQuantity =
            findViewById(R.id.tvQuantity)

        tvTotal =
            findViewById(R.id.tvCartTotal)

        productId =
            intent.getStringExtra("productId")

        codigoProducto =
            intent.getStringExtra("codigo")
                ?: intent.getStringExtra("producto")
                        ?: "air_pro"

        cargarProducto()

        findViewById<AppCompatButton>(
            R.id.btnDecrease
        ).setOnClickListener {

            if (cantidad > 1) {

                cantidad--

                actualizarTotal()
            }
        }

        findViewById<AppCompatButton>(
            R.id.btnIncrease
        ).setOnClickListener {

            cantidad++

            actualizarTotal()
        }

        findViewById<AppCompatButton>(
            R.id.btnCheckout
        ).setOnClickListener {

            if (precioUnitario <= 0) {

                Toast.makeText(
                    this,
                    "Espera mientras se carga el producto",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intent =
                Intent(
                    this,
                    CheckoutActivity::class.java
                )

            intent.putExtra(
                "productId",
                productId
            )

            intent.putExtra(
                "codigo",
                codigoProducto
            )

            intent.putExtra(
                "cantidad",
                cantidad
            )

            intent.putExtra(
                "total",
                precioUnitario * cantidad
            )

            startActivity(intent)
        }

        findViewById<AppCompatButton>(
            R.id.btnBack
        ).setOnClickListener {

            finish()
        }
    }

    private fun cargarProducto() {

        if (productId != null) {

            db.collection("productos")
                .document(productId!!)
                .get()
                .addOnSuccessListener { documento ->

                    if (documento.exists()) {

                        codigoProducto =
                            documento.getString("codigo")
                                ?: codigoProducto

                        mostrarProducto(
                            documento.getString("nombre")
                                ?: "Producto",

                            documento.getString("precio")
                                ?: "0"
                        )
                    }
                }

        } else {

            /*
             * Si el carrito se abre directamente desde
             * el menú inferior, mantiene Air Pro como ejemplo.
             */
            db.collection("productos")
                .whereEqualTo(
                    "codigo",
                    codigoProducto
                )
                .limit(1)
                .get()
                .addOnSuccessListener { resultado ->

                    if (!resultado.isEmpty) {

                        val documento =
                            resultado.documents[0]

                        productId =
                            documento.id

                        mostrarProducto(
                            documento.getString("nombre")
                                ?: "Producto",

                            documento.getString("precio")
                                ?: "0"
                        )
                    }
                }
        }
    }

    private fun mostrarProducto(
        nombre: String,
        precio: String
    ) {

        precioUnitario =
            precio.replace(
                Regex("[^0-9]"),
                ""
            ).toLongOrNull()
                ?: 0L

        tvProductName.text =
            nombre

        tvProductPrice.text =
            formatearPrecio(
                precioUnitario
            )

        actualizarTotal()
    }

    private fun actualizarTotal() {

        tvQuantity.text =
            cantidad.toString()

        tvTotal.text =
            formatearPrecio(
                precioUnitario * cantidad
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