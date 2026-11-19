package com.riatiga.audio

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private lateinit var tvCheckoutProduct: TextView
    private lateinit var tvCheckoutQuantity: TextView
    private lateinit var tvCheckoutTotal: TextView
    private lateinit var etDeliveryAddress: EditText
    private lateinit var btnUseLocation: AppCompatButton
    private lateinit var btnPay: AppCompatButton

    private var productId = ""
    private var codigoProducto = ""
    private var cantidad = 1
    private var total = 0L

    /*
     * Solicita permisos de ubicación.
     */
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permisos ->

            val permisoPreciso =
                permisos[Manifest.permission.ACCESS_FINE_LOCATION]
                    ?: false

            val permisoAproximado =
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION]
                    ?: false

            if (permisoPreciso || permisoAproximado) {

                obtenerUbicacion()

            } else {

                Toast.makeText(
                    this,
                    "No se concedió permiso de ubicación",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        db = FirebaseFirestore.getInstance()

        tvCheckoutProduct =
            findViewById(R.id.tvCheckoutProduct)

        tvCheckoutQuantity =
            findViewById(R.id.tvCheckoutQuantity)

        tvCheckoutTotal =
            findViewById(R.id.tvCheckoutTotal)

        etDeliveryAddress =
            findViewById(R.id.etDeliveryAddress)

        btnUseLocation =
            findViewById(R.id.btnUseLocation)

        btnPay =
            findViewById(R.id.btnPay)

        productId =
            intent.getStringExtra("productId") ?: ""

        codigoProducto =
            intent.getStringExtra("codigo") ?: ""

        cantidad =
            intent.getIntExtra("cantidad", 1)

        total =
            intent.getLongExtra("total", 0L)

        tvCheckoutQuantity.text =
            "Cantidad: $cantidad"

        tvCheckoutTotal.text =
            formatearPrecio(total)

        btnPay.text =
            "Pagar ${formatearPrecio(total)}"

        cargarProductoActual()

        /*
         * Obtiene ubicación real y la convierte
         * en una dirección legible.
         */
        btnUseLocation.setOnClickListener {

            comprobarPermisosUbicacion()
        }

        /*
         * El usuario todavía puede modificar
         * manualmente la dirección antes de pagar.
         */
        btnPay.setOnClickListener {

            val direccion =
                etDeliveryAddress.text
                    .toString()
                    .trim()

            if (direccion.isEmpty()) {

                Toast.makeText(
                    this,
                    "Ingresa una dirección de entrega",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            guardarPedido(direccion)
        }

        findViewById<AppCompatButton>(
            R.id.btnBack
        ).setOnClickListener {

            finish()
        }
    }

    /**
     * Comprueba los permisos de ubicación.
     */
    private fun comprobarPermisosUbicacion() {

        val permisoPreciso =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        val permisoAproximado =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

        if (
            permisoPreciso ==
            PackageManager.PERMISSION_GRANTED ||
            permisoAproximado ==
            PackageManager.PERMISSION_GRANTED
        ) {

            obtenerUbicacion()

        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    /**
     * Obtiene la ubicación actual.
     */
    private fun obtenerUbicacion() {

        val permisoPreciso =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        val permisoAproximado =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

        if (
            permisoPreciso != PackageManager.PERMISSION_GRANTED &&
            permisoAproximado != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        Toast.makeText(
            this,
            "Obteniendo ubicación...",
            Toast.LENGTH_SHORT
        ).show()

        val cancellationToken =
            CancellationTokenSource()

        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            )
            .addOnSuccessListener { ubicacion ->

                if (ubicacion != null) {

                    convertirCoordenadasADireccion(
                        ubicacion.latitude,
                        ubicacion.longitude
                    )

                } else {

                    Toast.makeText(
                        this,
                        "No fue posible obtener la ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Error de ubicación: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    /**
     * Convierte latitud y longitud
     * en una dirección legible.
     */
    private fun convertirCoordenadasADireccion(
        latitud: Double,
        longitud: Double
    ) {

        val geocoder =
            Geocoder(
                this,
                Locale("es", "CO")
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            geocoder.getFromLocation(
                latitud,
                longitud,
                1
            ) { direcciones ->

                runOnUiThread {

                    if (direcciones.isNotEmpty()) {

                        val direccion =
                            direcciones[0]
                                .getAddressLine(0)

                        etDeliveryAddress.setText(
                            direccion
                        )

                        Toast.makeText(
                            this,
                            "Ubicación encontrada",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        mostrarCoordenadas(
                            latitud,
                            longitud
                        )
                    }
                }
            }

        } else {

            try {

                @Suppress("DEPRECATION")
                val direcciones =
                    geocoder.getFromLocation(
                        latitud,
                        longitud,
                        1
                    )

                if (!direcciones.isNullOrEmpty()) {

                    val direccion =
                        direcciones[0]
                            .getAddressLine(0)

                    etDeliveryAddress.setText(
                        direccion
                    )

                    Toast.makeText(
                        this,
                        "Ubicación encontrada",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    mostrarCoordenadas(
                        latitud,
                        longitud
                    )
                }

            } catch (e: Exception) {

                mostrarCoordenadas(
                    latitud,
                    longitud
                )
            }
        }
    }

    /**
     * Si no se puede obtener una dirección,
     * deja las coordenadas como respaldo.
     */
    private fun mostrarCoordenadas(
        latitud: Double,
        longitud: Double
    ) {

        val lat =
            String.format(
                Locale.US,
                "%.6f",
                latitud
            )

        val lon =
            String.format(
                Locale.US,
                "%.6f",
                longitud
            )

        etDeliveryAddress.setText(
            "Lat: $lat, Long: $lon"
        )

        Toast.makeText(
            this,
            "Se obtuvo la ubicación, pero no la dirección",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Carga el producto actual desde Firestore.
     */
    private fun cargarProductoActual() {

        if (productId.isNotEmpty()) {

            db.collection("productos")
                .document(productId)
                .get()
                .addOnSuccessListener { documento ->

                    if (documento.exists()) {

                        val nombre =
                            documento.getString("nombre")
                                ?: "Producto"

                        val precio =
                            (documento.get("precio") as? Number)
                                ?.toLong() ?: 0L

                        tvCheckoutProduct.text =
                            nombre

                        if (precio > 0) {

                            total =
                                precio * cantidad

                            tvCheckoutTotal.text =
                                formatearPrecio(total)

                            btnPay.text =
                                "Pagar ${formatearPrecio(total)}"
                        }
                    }
                }

        } else if (codigoProducto.isNotEmpty()) {

            db.collection("productos")
                .whereEqualTo(
                    "codigo",
                    codigoProducto
                )
                .limit(1)
                .get()
                .addOnSuccessListener { resultado ->

                    val documento =
                        resultado.documents.firstOrNull()
                            ?: return@addOnSuccessListener

                    productId =
                        documento.id

                    val nombre =
                        documento.getString("nombre")
                            ?: "Producto"

                    val precio =
                        (documento.get("precio") as? Number)
                            ?.toLong() ?: 0L

                    tvCheckoutProduct.text =
                        nombre

                    if (precio > 0) {

                        total =
                            precio * cantidad

                        tvCheckoutTotal.text =
                            formatearPrecio(total)

                        btnPay.text =
                            "Pagar ${formatearPrecio(total)}"
                    }
                }
        }
    }

    /**
     * Guarda el pedido en Firestore.
     */
    private fun guardarPedido(
        direccion: String
    ) {

        val correoComprador =
            FirebaseAuth.getInstance()
                .currentUser
                ?.email
                ?: "Comprador"

        val pedido =
            hashMapOf(
                "productId" to productId,
                "codigo" to codigoProducto,
                "producto" to tvCheckoutProduct.text.toString(),
                "cantidad" to cantidad,
                "total" to total,
                "direccion" to direccion,
                "comprador" to correoComprador,
                "estado" to "Preparando",
                "fecha" to FieldValue.serverTimestamp()
            )

        btnPay.isEnabled = false

        db.collection("pedidos")
            .add(pedido)
            .addOnSuccessListener {

                mostrarCompraExitosa()
            }
            .addOnFailureListener { error ->

                btnPay.isEnabled = true

                Toast.makeText(
                    this,
                    "Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    /**
     * Mensaje de compra exitosa.
     */
    private fun mostrarCompraExitosa() {

        AlertDialog.Builder(this)
            .setTitle("Pago realizado")
            .setMessage(
                "La compra fue procesada correctamente.\n\n" +
                        "Total: ${formatearPrecio(total)}"
            )
            .setPositiveButton(
                "Aceptar"
            ) { _, _ ->

                val intent =
                    Intent(
                        this,
                        BuyerHomeActivity::class.java
                    )

                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP

                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Convierte 849900 en $849.900.
     */
    private fun formatearPrecio(
        valor: Long
    ): String {

        val numero =
            String.format(
                Locale.US,
                "%,d",
                valor
            ).replace(",", ".")

        return "$$numero"
    }
}