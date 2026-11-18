package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Catálogo del comprador.
 * Los productos se obtienen directamente desde Firestore
 * y se identifican mediante su ID y código interno.
 */
class BuyerCatalogActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var productsContainer: LinearLayout

    data class Producto(
        val id: String,
        val nombre: String,
        val precio: String,
        val stock: String,
        val caracteristicas: String,
        val codigo: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buyer_catalog)

        db = FirebaseFirestore.getInstance()

        productsContainer =
            findViewById(R.id.productsContainer)

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

        findViewById<AppCompatButton>(
            R.id.btnBack
        ).setOnClickListener {

            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        cargarProductos()
    }

    /**
     * Consulta todos los productos guardados en Firestore.
     */
    private fun cargarProductos() {

        db.collection("productos")
            .get()
            .addOnSuccessListener { resultado ->

                productsContainer.removeAllViews()

                if (resultado.isEmpty) {

                    val mensaje =
                        TextView(this).apply {

                            text =
                                "No hay productos disponibles"

                            setTextColor(
                                getColor(
                                    R.color.text_secondary
                                )
                            )

                            textSize = 16f

                            setPadding(
                                0,
                                resources.getDimensionPixelSize(
                                    R.dimen.space_xl
                                ),
                                0,
                                0
                            )
                        }

                    productsContainer.addView(mensaje)

                    return@addOnSuccessListener
                }

                for (documento in resultado.documents) {

                    val producto =
                        Producto(
                            id = documento.id,

                            nombre =
                                documento.getString("nombre")
                                    ?: "Producto",

                            precio =
                                documento.getString("precio")
                                    ?: "0",

                            stock =
                                documento.getString("stock")
                                    ?: "0",

                            caracteristicas =
                                documento.getString("caracteristicas")
                                    ?: "",

                            codigo =
                                documento.getString("codigo")
                                    ?: "generico"
                        )

                    productsContainer.addView(
                        crearTarjetaProducto(producto)
                    )
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "No fue posible cargar el catálogo",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    /**
     * Crea la tarjeta visual de cada producto.
     */
    private fun crearTarjetaProducto(
        producto: Producto
    ): View {

        val espacioSm =
            resources.getDimensionPixelSize(
                R.dimen.space_sm
            )

        val espacioMd =
            resources.getDimensionPixelSize(
                R.dimen.space_md
            )

        val espacioLg =
            resources.getDimensionPixelSize(
                R.dimen.space_lg
            )

        val card =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setBackgroundResource(
                    R.drawable.bg_card
                )

                setPadding(
                    espacioLg,
                    espacioLg,
                    espacioLg,
                    espacioLg
                )

                isClickable = true
                isFocusable = true

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {

                        topMargin = espacioMd
                    }
            }

        /*
         * La imagen depende del código interno,
         * no del nombre visible del producto.
         */
        val imagenProducto =
            obtenerImagenProducto(
                producto.codigo
            )

        if (imagenProducto != null) {

            val imagen =
                ImageView(this).apply {

                    setImageResource(
                        imagenProducto
                    )

                    contentDescription =
                        producto.nombre

                    scaleType =
                        ImageView.ScaleType.FIT_CENTER

                    adjustViewBounds = true

                    layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(180)
                        )
                }

            card.addView(imagen)
        }

        val tvNombre =
            TextView(this).apply {

                text =
                    producto.nombre

                setTextColor(
                    getColor(
                        R.color.text_primary
                    )
                )

                textSize = 18f

                setTypeface(
                    typeface,
                    android.graphics.Typeface.BOLD
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {

                        topMargin = espacioMd
                    }
            }

        card.addView(tvNombre)

        val tvPrecio =
            TextView(this).apply {

                text =
                    formatearPrecio(
                        producto.precio
                    )

                setTextColor(
                    getColor(
                        R.color.text_primary
                    )
                )

                textSize = 16f

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {

                        topMargin = espacioSm
                    }
            }

        card.addView(tvPrecio)

        val tvCaracteristicas =
            TextView(this).apply {

                text =
                    producto.caracteristicas

                setTextColor(
                    getColor(
                        R.color.text_secondary
                    )
                )

                textSize = 12f

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {

                        topMargin = espacioSm
                    }
            }

        card.addView(tvCaracteristicas)

        val tvStock =
            TextView(this).apply {

                text =
                    "Disponibles: ${producto.stock}"

                setTextColor(
                    getColor(
                        R.color.text_secondary
                    )
                )

                textSize = 12f

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {

                        topMargin = espacioSm
                    }
            }

        card.addView(tvStock)

        val tvDetalle =
            TextView(this).apply {

                text =
                    getString(
                        R.string.product_detail
                    )

                setTextColor(
                    getColor(
                        R.color.text_primary
                    )
                )

                setTypeface(
                    typeface,
                    android.graphics.Typeface.BOLD
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {

                        topMargin = espacioMd
                    }
            }

        card.addView(tvDetalle)

        card.setOnClickListener {

            val intent =
                Intent(
                    this,
                    ProductDetailActivity::class.java
                )

            /*
             * Estos son los datos importantes nuevos.
             */
            intent.putExtra(
                "productId",
                producto.id
            )

            intent.putExtra(
                "codigo",
                producto.codigo
            )

            /*
             * Lo mantenemos temporalmente para que
             * el detalle antiguo siga funcionando
             * mientras terminamos la migración.
             */
            intent.putExtra(
                "producto",
                producto.codigo
            )

            startActivity(intent)
        }

        return card
    }

    /**
     * La imagen se selecciona mediante el código interno.
     * Por eso cambiar el nombre no cambia la imagen.
     */
    private fun obtenerImagenProducto(
        codigo: String
    ): Int? {

        return when (codigo) {

            "air_pro" ->
                R.drawable.air_pro

            "one" ->
                R.drawable.riatiga_one

            else ->
                null
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

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }
}