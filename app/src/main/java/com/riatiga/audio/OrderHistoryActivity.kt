package com.riatiga.audio

import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla de pedidos del vendedor.
 *
 * Permite:
 * - Consultar pedidos.
 * - Actualizar su estado.
 * - Eliminar pedidos.
 */
class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var ordersContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        db = FirebaseFirestore.getInstance()

        ordersContainer = findViewById(R.id.ordersContainer)

        findViewById<AppCompatButton>(
            R.id.btnBack
        ).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        cargarPedidos()
    }

    /**
     * Consulta los pedidos almacenados en Firestore.
     */
    private fun cargarPedidos() {

        ordersContainer.removeAllViews()

        db.collection("pedidos")
            .orderBy(
                "fecha",
                Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener { resultado ->

                ordersContainer.removeAllViews()

                if (resultado.isEmpty) {

                    mostrarMensajeSinPedidos()
                    return@addOnSuccessListener
                }

                for (documento in resultado.documents) {

                    val pedidoId = documento.id

                    val producto =
                        documento.getString("producto")
                            ?: "Producto"

                    val comprador =
                        documento.getString("comprador")
                            ?: "Sin comprador"

                    val direccion =
                        documento.getString("direccion")
                            ?: "Sin dirección"

                    val estado =
                        documento.getString("estado")
                            ?: "Preparando"

                    val cantidad =
                        (documento.get("cantidad") as? Number)
                            ?.toLong() ?: 0L

                    val total =
                        (documento.get("total") as? Number)
                            ?.toLong() ?: 0L

                    val fecha =
                        documento.getTimestamp("fecha")

                    crearTarjetaPedido(
                        pedidoId,
                        producto,
                        comprador,
                        direccion,
                        estado,
                        cantidad,
                        total,
                        fecha
                    )
                }
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Error al cargar pedidos: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    /**
     * Crea visualmente una tarjeta para cada pedido.
     */
    private fun crearTarjetaPedido(
        pedidoId: String,
        producto: String,
        comprador: String,
        direccion: String,
        estado: String,
        cantidad: Long,
        total: Long,
        fecha: Timestamp?
    ) {

        val tarjeta = LinearLayout(this)

        tarjeta.orientation =
            LinearLayout.VERTICAL

        tarjeta.setPadding(
            dp(20),
            dp(20),
            dp(20),
            dp(20)
        )

        tarjeta.setBackgroundResource(
            R.drawable.bg_card
        )

        val parametrosTarjeta =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        parametrosTarjeta.setMargins(
            0,
            0,
            0,
            dp(16)
        )

        tarjeta.layoutParams =
            parametrosTarjeta

        // Número del pedido
        val tvPedido =
            crearTexto(
                "Pedido #${pedidoId.take(8).uppercase()}",
                18f,
                true
            )

        tarjeta.addView(tvPedido)

        // Producto
        tarjeta.addView(
            crearTexto(
                producto,
                16f,
                true
            )
        )

        // Cantidad
        tarjeta.addView(
            crearTexto(
                "Cantidad: $cantidad",
                14f,
                false
            )
        )

        // Total
        tarjeta.addView(
            crearTexto(
                "Total: ${formatearPrecio(total)}",
                14f,
                true
            )
        )

        // Comprador
        tarjeta.addView(
            crearTexto(
                "Comprador: $comprador",
                14f,
                false
            )
        )

        // Dirección
        tarjeta.addView(
            crearTexto(
                "Dirección: $direccion",
                14f,
                false
            )
        )

        // Estado
        val tvEstado =
            crearTexto(
                "Estado: $estado",
                14f,
                true
            )

        tarjeta.addView(tvEstado)

        // Fecha
        tarjeta.addView(
            crearTexto(
                "Fecha: ${formatearFecha(fecha)}",
                14f,
                false
            )
        )

        // Contenedor de botones
        val botones =
            LinearLayout(this)

        botones.orientation =
            LinearLayout.HORIZONTAL

        val parametrosBotones =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        parametrosBotones.setMargins(
            0,
            dp(16),
            0,
            0
        )

        botones.layoutParams =
            parametrosBotones

        // Botón para cambiar estado
        val btnEstado =
            AppCompatButton(this)

        btnEstado.isAllCaps = false

        btnEstado.text =
            when (estado) {

                "Preparando" ->
                    "Marcar enviado"

                "Enviado" ->
                    "Marcar entregado"

                else ->
                    "Entregado"
            }

        val paramsEstado =
            LinearLayout.LayoutParams(
                0,
                dp(50),
                1f
            )

        btnEstado.layoutParams =
            paramsEstado

        /*
         * Si ya está entregado,
         * no se puede seguir avanzando.
         */
        btnEstado.isEnabled =
            estado != "Entregado"

        btnEstado.setOnClickListener {

            actualizarEstado(
                pedidoId,
                estado
            )
        }

        botones.addView(btnEstado)

        // Espacio entre botones
        val espacio =
            View(this)

        espacio.layoutParams =
            LinearLayout.LayoutParams(
                dp(8),
                1
            )

        botones.addView(espacio)

        // Botón eliminar
        val btnEliminar =
            AppCompatButton(this)

        btnEliminar.text =
            "Eliminar"

        btnEliminar.isAllCaps =
            false

        val paramsEliminar =
            LinearLayout.LayoutParams(
                0,
                dp(50),
                1f
            )

        btnEliminar.layoutParams =
            paramsEliminar

        btnEliminar.setOnClickListener {

            confirmarEliminar(
                pedidoId
            )
        }

        botones.addView(btnEliminar)

        tarjeta.addView(botones)

        ordersContainer.addView(tarjeta)
    }

    /**
     * Cambia el estado del pedido:
     *
     * Preparando -> Enviado -> Entregado
     */
    private fun actualizarEstado(
        pedidoId: String,
        estadoActual: String
    ) {

        val nuevoEstado =
            when (estadoActual) {

                "Preparando" ->
                    "Enviado"

                "Enviado" ->
                    "Entregado"

                else ->
                    return
            }

        db.collection("pedidos")
            .document(pedidoId)
            .update(
                "estado",
                nuevoEstado
            )
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Pedido actualizado a $nuevoEstado",
                    Toast.LENGTH_SHORT
                ).show()

                cargarPedidos()
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    /**
     * Solicita confirmación antes de borrar un pedido.
     */
    private fun confirmarEliminar(
        pedidoId: String
    ) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar pedido")
            .setMessage(
                "¿Deseas eliminar este pedido?"
            )
            .setPositiveButton(
                "Eliminar"
            ) { _, _ ->

                eliminarPedido(
                    pedidoId
                )
            }
            .setNegativeButton(
                "Cancelar",
                null
            )
            .show()
    }

    /**
     * Elimina el documento de Firestore.
     */
    private fun eliminarPedido(
        pedidoId: String
    ) {

        db.collection("pedidos")
            .document(pedidoId)
            .delete()
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Pedido eliminado",
                    Toast.LENGTH_SHORT
                ).show()

                cargarPedidos()
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    /**
     * Muestra mensaje cuando no existen pedidos.
     */
    private fun mostrarMensajeSinPedidos() {

        val mensaje =
            crearTexto(
                "No hay pedidos registrados",
                16f,
                false
            )

        ordersContainer.addView(mensaje)
    }

    /**
     * Crea los TextView utilizados en cada tarjeta.
     */
    private fun crearTexto(
        texto: String,
        tamano: Float,
        negrita: Boolean
    ): TextView {

        val textView =
            TextView(this)

        textView.text =
            texto

        textView.textSize =
            tamano

        textView.setTextColor(
            getColor(R.color.text_primary)
        )

        if (negrita) {
            textView.setTypeface(
                null,
                Typeface.BOLD
            )
        }

        val parametros =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        parametros.setMargins(
            0,
            dp(6),
            0,
            0
        )

        textView.layoutParams =
            parametros

        return textView
    }

    /**
     * Formatea la fecha del pedido.
     */
    private fun formatearFecha(
        fecha: Timestamp?
    ): String {

        if (fecha == null) {
            return "Sin fecha"
        }

        val formato =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            )

        return formato.format(
            fecha.toDate()
        )
    }

    /**
     * Convierte 849900 en $849.900.
     */
    private fun formatearPrecio(
        valor: Long
    ): String {

        val formato =
            NumberFormat.getNumberInstance(
                Locale("es", "CO")
            )

        return "$${formato.format(valor)}"
    }

    /**
     * Convierte dp a píxeles.
     */
    private fun dp(valor: Int): Int {

        return (
                valor *
                        resources.displayMetrics.density
                ).toInt()
    }
}