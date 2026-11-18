package com.riatiga.audio

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Formulario para crear y editar productos almacenados en Firestore.
 *
 * Cada producto conserva un código interno que permite identificarlo
 * aunque posteriormente cambie su nombre.
 */
class ProductFormActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private var productId: String? = null
    private var codigoProducto = "generico"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_form)

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

        val tvTitle =
            findViewById<TextView>(R.id.tvProductFormTitle)

        val etName =
            findViewById<EditText>(R.id.etProductName)

        val etPrice =
            findViewById<EditText>(R.id.etProductPrice)

        val etStock =
            findViewById<EditText>(R.id.etProductStock)

        val etFeatures =
            findViewById<EditText>(R.id.etProductFeatures)

        productId =
            intent.getStringExtra("productId")

        if (productId != null) {

            tvTitle.text =
                getString(R.string.edit_product)

            cargarProducto(
                productId!!,
                etName,
                etPrice,
                etStock,
                etFeatures
            )

        } else {

            tvTitle.text =
                getString(R.string.new_product)
        }

        // Guardar producto
        findViewById<AppCompatButton>(
            R.id.btnSaveProduct
        ).setOnClickListener {

            val nombre =
                etName.text.toString().trim()

            val precio =
                etPrice.text.toString().trim()

            val stock =
                etStock.text.toString().trim()

            val caracteristicas =
                etFeatures.text.toString().trim()

            if (
                nombre.isEmpty() ||
                precio.isEmpty() ||
                stock.isEmpty() ||
                caracteristicas.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    getString(R.string.complete_all_fields),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            /*
             * Solo cuando el producto es nuevo se determina
             * inicialmente qué imagen local le corresponde.
             *
             * Después ese código se conserva aunque cambie el nombre.
             */
            if (productId == null) {

                codigoProducto =
                    obtenerCodigoInicial(nombre)
            }

            val producto =
                hashMapOf(
                    "nombre" to nombre,
                    "precio" to precio,
                    "stock" to stock,
                    "caracteristicas" to caracteristicas,
                    "codigo" to codigoProducto
                )

            if (productId == null) {

                db.collection("productos")
                    .add(producto)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            getString(R.string.product_created),
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "No fue posible crear el producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            } else {

                db.collection("productos")
                    .document(productId!!)
                    .set(producto)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            getString(R.string.product_updated),
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "No fue posible actualizar el producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        findViewById<AppCompatButton>(
            R.id.btnCancel
        ).setOnClickListener {

            finish()
        }
    }

    /**
     * Carga el producto seleccionado directamente por su ID de Firestore.
     */
    private fun cargarProducto(
        id: String,
        etName: EditText,
        etPrice: EditText,
        etStock: EditText,
        etFeatures: EditText
    ) {

        db.collection("productos")
            .document(id)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    val nombre =
                        documento.getString("nombre") ?: ""

                    etName.setText(nombre)

                    etPrice.setText(
                        documento.getString("precio") ?: ""
                    )

                    etStock.setText(
                        documento.getString("stock") ?: ""
                    )

                    etFeatures.setText(
                        documento.getString("caracteristicas") ?: ""
                    )

                    /*
                     * Si el producto ya tenía código se conserva.
                     * Si es un producto antiguo, se determina una sola vez.
                     */
                    codigoProducto =
                        documento.getString("codigo")
                            ?: obtenerCodigoInicial(nombre)
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "No fue posible cargar el producto",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    /**
     * Determina qué imagen local corresponde inicialmente al producto.
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
}