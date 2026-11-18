package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Detalle del producto.
 * El producto se consulta mediante su ID real de Firestore.
 */
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private var productId: String? = null
    private var codigoProducto = "air_pro"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)

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

        productId =
            intent.getStringExtra("productId")

        codigoProducto =
            intent.getStringExtra("codigo")
                ?: intent.getStringExtra("producto")
                        ?: "air_pro"

        mostrarImagen()

        if (productId != null) {

            cargarProductoPorId(
                productId!!
            )

        } else {

            /*
             * Compatibilidad por si se abre esta pantalla
             * desde alguna parte antigua de la app.
             */
            buscarProductoPorCodigo()
        }

        findViewById<AppCompatButton>(
            R.id.btnAddToCart
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    CartActivity::class.java
                )

            intent.putExtra(
                "productId",
                productId
            )

            intent.putExtra(
                "codigo",
                codigoProducto
            )

            startActivity(intent)
        }

        findViewById<AppCompatButton>(
            R.id.btnBack
        ).setOnClickListener {

            finish()
        }
    }

    /**
     * Muestra la imagen local según el código interno.
     */
    private fun mostrarImagen() {

        val imagen =
            findViewById<ImageView>(
                R.id.ivProductImage
            )

        when (codigoProducto) {

            "one" ->
                imagen.setImageResource(
                    R.drawable.riatiga_one
                )

            else ->
                imagen.setImageResource(
                    R.drawable.air_pro
                )
        }
    }

    /**
     * Consulta directamente el documento por su ID.
     */
    private fun cargarProductoPorId(
        id: String
    ) {

        db.collection("productos")
            .document(id)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    codigoProducto =
                        documento.getString("codigo")
                            ?: codigoProducto

                    mostrarImagen()

                    mostrarDatos(
                        documento.getString("nombre")
                            ?: "Producto",

                        documento.getString("precio")
                            ?: "0",

                        documento.getString("caracteristicas")
                            ?: ""
                    )

                } else {

                    Toast.makeText(
                        this,
                        "Producto no encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    /**
     * Busca por código únicamente como compatibilidad.
     */
    private fun buscarProductoPorCodigo() {

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

                    mostrarDatos(
                        documento.getString("nombre")
                            ?: "Producto",

                        documento.getString("precio")
                            ?: "0",

                        documento.getString("caracteristicas")
                            ?: ""
                    )
                }
            }
    }

    private fun mostrarDatos(
        nombre: String,
        precio: String,
        caracteristicas: String
    ) {

        findViewById<TextView>(
            R.id.tvProductName
        ).text =
            nombre

        findViewById<TextView>(
            R.id.tvProductPrice
        ).text =
            formatearPrecio(precio)

        findViewById<TextView>(
            R.id.tvProductFeatures
        ).text =
            caracteristicas
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