package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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
 * Permite crear, consultar, editar y eliminar
 * productos almacenados en Firestore.
 */
class ProductManagementActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_product_management)

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
            R.id.btnNewProduct
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProductFormActivity::class.java
                )
            )
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
     * Consulta todos los productos de Firestore.
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
                                "No hay productos registrados"

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

                    val nombre =
                        documento.getString("nombre")
                            ?: "Producto"

                    /*
                     * Productos creados antes de este cambio
                     * todavía no tienen el campo codigo.
                     */
                    var codigo =
                        documento.getString("codigo")

                    if (codigo.isNullOrEmpty()) {

                        codigo =
                            obtenerCodigoInicial(nombre)

                        /*
                         * Se guarda automáticamente el código
                         * para no volver a depender del nombre.
                         */
                        documento.reference.update(
                            "codigo",
                            codigo
                        )
                    }

                    val producto =
                        Producto(
                            id = documento.id,

                            nombre = nombre,

                            precio =
                                documento.getString("precio")
                                    ?: "0",

                            stock =
                                documento.getString("stock")
                                    ?: "0",

                            caracteristicas =
                                documento.getString("caracteristicas")
                                    ?: "",

                            codigo = codigo
                        )

                    productsContainer.addView(
                        crearTarjetaProducto(producto)
                    )
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "No fue posible cargar los productos",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

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

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {

                        topMargin = espacioMd
                    }
            }

        val tvNombre =
            TextView(this).apply {

                text = producto.nombre

                setTextColor(
                    getColor(R.color.text_primary)
                )

                textSize = 18f

                setTypeface(
                    typeface,
                    android.graphics.Typeface.BOLD
                )
            }

        card.addView(tvNombre)

        val tvPrecio =
            TextView(this).apply {

                text =
                    formatearPrecio(
                        producto.precio
                    )

                setTextColor(
                    getColor(R.color.text_primary)
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

        val tvStock =
            TextView(this).apply {

                text =
                    "Stock: ${producto.stock} unidades"

                setTextColor(
                    getColor(R.color.text_secondary)
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

        val tvCaracteristicas =
            TextView(this).apply {

                text =
                    producto.caracteristicas

                setTextColor(
                    getColor(R.color.text_secondary)
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

        card.addView(tvCaracteristicas)

        val botones =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {

                        topMargin = espacioLg
                    }
            }

        val btnEditar =
            AppCompatButton(this).apply {

                text =
                    getString(R.string.edit)

                isAllCaps = false

                setBackgroundResource(
                    R.drawable.bg_input
                )

                setTextColor(
                    getColor(R.color.text_primary)
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        resources.getDimensionPixelSize(
                            R.dimen.button_height
                        ),
                        1f
                    )

                setOnClickListener {

                    abrirProducto(
                        producto.id
                    )
                }
            }

        botones.addView(btnEditar)

        val espacio =
            View(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        espacioSm,
                        1
                    )
            }

        botones.addView(espacio)

        val btnEliminar =
            AppCompatButton(this).apply {

                text =
                    getString(R.string.delete)

                isAllCaps = false

                setBackgroundResource(
                    R.drawable.bg_input
                )

                setTextColor(
                    getColor(R.color.error)
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        resources.getDimensionPixelSize(
                            R.dimen.button_height
                        ),
                        1f
                    )

                setOnClickListener {

                    confirmarEliminar(
                        producto
                    )
                }
            }

        botones.addView(btnEliminar)

        card.addView(botones)

        return card
    }

    /**
     * Abre el producto directamente mediante
     * su ID real de Firestore.
     */
    private fun abrirProducto(
        id: String
    ) {

        val intent =
            Intent(
                this,
                ProductFormActivity::class.java
            )

        intent.putExtra(
            "productId",
            id
        )

        startActivity(intent)
    }

    private fun confirmarEliminar(
        producto: Producto
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                getString(R.string.delete_product)
            )
            .setMessage(
                getString(
                    R.string.delete_product_confirmation,
                    producto.nombre
                )
            )
            .setNegativeButton(
                getString(R.string.cancel),
                null
            )
            .setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->

                db.collection("productos")
                    .document(producto.id)
                    .delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Producto eliminado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarProductos()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "No fue posible eliminar el producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .show()
    }

    /**
     * Asigna el código de la imagen local.
     */
    private fun obtenerCodigoInicial(
        nombre: String
    ): String {

        val nombreNormalizado =
            nombre.lowercase()

        return when {

            nombreNormalizado.contains("air pro") ->
                "air_pro"

            nombreNormalizado.contains("riatiga one") ||
                    nombreNormalizado == "one" ->
                "one"

            else ->
                "generico"
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