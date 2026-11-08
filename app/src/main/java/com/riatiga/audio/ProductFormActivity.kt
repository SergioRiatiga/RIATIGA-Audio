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

class ProductFormActivity : AppCompatActivity() {

    private var productoSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_form)

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

        val tvTitle = findViewById<TextView>(R.id.tvProductFormTitle)

        val etName = findViewById<EditText>(R.id.etProductName)
        val etPrice = findViewById<EditText>(R.id.etProductPrice)
        val etStock = findViewById<EditText>(R.id.etProductStock)
        val etFeatures = findViewById<EditText>(R.id.etProductFeatures)

        productoSeleccionado = intent.getStringExtra("producto")

        /*
         * Si no recibimos un producto, el formulario funciona
         * como creación de un producto nuevo.
         *
         * Si recibimos "air_pro" o "one", funciona como edición.
         */
        when (productoSeleccionado) {

            "air_pro" -> {
                tvTitle.text = getString(R.string.edit_product)

                etName.setText(getString(R.string.air_pro))
                etPrice.setText(getString(R.string.air_pro_price_input))
                etStock.setText(getString(R.string.air_pro_stock_input))
                etFeatures.setText(getString(R.string.product_features))
            }

            "one" -> {
                tvTitle.text = getString(R.string.edit_product)

                etName.setText(getString(R.string.one))
                etPrice.setText(getString(R.string.one_price_input))
                etStock.setText(getString(R.string.one_stock_input))
                etFeatures.setText(getString(R.string.one_features))
            }

            else -> {
                tvTitle.text = getString(R.string.new_product)
            }
        }

        // Guardar producto
        findViewById<AppCompatButton>(R.id.btnSaveProduct).setOnClickListener {

            val nombre = etName.text.toString().trim()
            val precio = etPrice.text.toString().trim()
            val stock = etStock.text.toString().trim()
            val caracteristicas = etFeatures.text.toString().trim()

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

            if (productoSeleccionado == null) {

                Toast.makeText(
                    this,
                    getString(R.string.product_created),
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    getString(R.string.product_updated),
                    Toast.LENGTH_SHORT
                ).show()
            }

            finish()
        }

        // Cancelar
        findViewById<AppCompatButton>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }
}