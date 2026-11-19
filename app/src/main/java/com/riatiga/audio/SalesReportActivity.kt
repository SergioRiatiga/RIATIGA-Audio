package com.riatiga.audio

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

/**
 * Reporte de ventas del administrador.
 * Los valores se calculan usando los pedidos guardados en Firestore.
 */
class SalesReportActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var tvTotalRevenue: TextView
    private lateinit var tvTotalOrders: TextView
    private lateinit var tvUnitsSold: TextView

    private lateinit var tvAirProName: TextView
    private lateinit var tvAirProUnits: TextView
    private lateinit var tvAirProSales: TextView

    private lateinit var tvOneName: TextView
    private lateinit var tvOneUnits: TextView
    private lateinit var tvOneSales: TextView

    private lateinit var progressAirPro: ProgressBar
    private lateinit var progressOne: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sales_report)

        db = FirebaseFirestore.getInstance()

        tvTotalRevenue = findViewById(R.id.tvTotalRevenue)
        tvTotalOrders = findViewById(R.id.tvTotalOrders)
        tvUnitsSold = findViewById(R.id.tvUnitsSold)

        tvAirProName = findViewById(R.id.tvAirProName)
        tvAirProUnits = findViewById(R.id.tvAirProUnits)
        tvAirProSales = findViewById(R.id.tvAirProSales)

        tvOneName = findViewById(R.id.tvOneName)
        tvOneUnits = findViewById(R.id.tvOneUnits)
        tvOneSales = findViewById(R.id.tvOneSales)

        progressAirPro = findViewById(R.id.progressAirPro)
        progressOne = findViewById(R.id.progressOne)

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

        // Actualiza el reporte cada vez que se abre la pantalla.
        cargarReporte()
        cargarNombresProductos()
    }

    /**
     * Lee todos los pedidos y calcula:
     * ingresos, cantidad de pedidos, unidades vendidas
     * y ventas de cada producto.
     */
    private fun cargarReporte() {

        db.collection("pedidos")
            .get()
            .addOnSuccessListener { resultado ->

                var ingresosTotales = 0L
                var unidadesTotales = 0L

                var unidadesAirPro = 0L
                var ventasAirPro = 0L

                var unidadesOne = 0L
                var ventasOne = 0L

                for (documento in resultado.documents) {

                    val codigo =
                        documento.getString("codigo") ?: ""

                    val cantidad =
                        (documento.get("cantidad") as? Number)
                            ?.toLong() ?: 0L

                    val total =
                        (documento.get("total") as? Number)
                            ?.toLong() ?: 0L

                    ingresosTotales += total
                    unidadesTotales += cantidad

                    when (codigo) {

                        "air_pro" -> {
                            unidadesAirPro += cantidad
                            ventasAirPro += total
                        }

                        "one" -> {
                            unidadesOne += cantidad
                            ventasOne += total
                        }
                    }
                }

                // Resumen general
                tvTotalRevenue.text =
                    formatearPrecio(ingresosTotales)

                tvTotalOrders.text =
                    resultado.size().toString()

                tvUnitsSold.text =
                    unidadesTotales.toString()

                // Air Pro
                tvAirProUnits.text =
                    "$unidadesAirPro unidades vendidas"

                tvAirProSales.text =
                    formatearPrecio(ventasAirPro)

                // One
                tvOneUnits.text =
                    "$unidadesOne unidades vendidas"

                tvOneSales.text =
                    formatearPrecio(ventasOne)

                actualizarBarras(
                    unidadesAirPro,
                    unidadesOne
                )
            }
    }

    /**
     * Busca los nombres actuales de los productos.
     * Así, si el administrador cambia un nombre,
     * el reporte también lo muestra actualizado.
     */
    private fun cargarNombresProductos() {

        db.collection("productos")
            .get()
            .addOnSuccessListener { resultado ->

                for (documento in resultado.documents) {

                    val codigo =
                        documento.getString("codigo") ?: ""

                    val nombre =
                        documento.getString("nombre") ?: ""

                    when (codigo) {

                        "air_pro" -> {
                            tvAirProName.text = nombre
                        }

                        "one" -> {
                            tvOneName.text = nombre
                        }
                    }
                }
            }
    }

    /**
     * Las barras muestran qué porcentaje de las
     * unidades vendidas corresponde a cada producto.
     */
    private fun actualizarBarras(
        unidadesAirPro: Long,
        unidadesOne: Long
    ) {

        val total =
            unidadesAirPro + unidadesOne

        if (total == 0L) {

            progressAirPro.progress = 0
            progressOne.progress = 0

            return
        }

        val porcentajeAirPro =
            ((unidadesAirPro * 100) / total).toInt()

        val porcentajeOne =
            ((unidadesOne * 100) / total).toInt()

        progressAirPro.progress =
            porcentajeAirPro

        progressOne.progress =
            porcentajeOne
    }

    /**
     * Convierte 849900 en $849.900
     */
    private fun formatearPrecio(valor: Long): String {

        val numero =
            String.format(
                Locale.US,
                "%,d",
                valor
            ).replace(",", ".")

        return "$$numero"
    }
}