package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
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
 * Permite al administrador consultar, buscar,
 * editar y eliminar usuarios almacenados en Firestore.
 */
class UserManagementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var usersContainer: LinearLayout
    private lateinit var etSearchUser: EditText

    private val usuarios = mutableListOf<Usuario>()

    data class Usuario(
        val nombre: String,
        val correo: String,
        val telefono: String,
        val rol: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_management)

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

        usersContainer =
            findViewById(R.id.usersContainer)

        etSearchUser =
            findViewById(R.id.etSearchUser)

        // Crear nuevo usuario
        findViewById<AppCompatButton>(
            R.id.btnNewUser
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    UserFormActivity::class.java
                )
            )
        }

        // Buscar usuarios
        findViewById<AppCompatButton>(
            R.id.btnFilterUsers
        ).setOnClickListener {

            filtrarUsuarios()
        }

        // Volver
        findViewById<AppCompatButton>(
            R.id.btnBack
        ).setOnClickListener {

            finish()
        }
    }

    /**
     * Cada vez que se vuelve a esta pantalla,
     * consulta nuevamente los usuarios de Firestore.
     */
    override fun onResume() {
        super.onResume()

        cargarUsuarios()
    }

    /**
     * Consulta todos los documentos de la
     * colección usuarios en Firestore.
     */
    private fun cargarUsuarios() {

        db.collection("usuarios")
            .get()
            .addOnSuccessListener { resultado ->

                usuarios.clear()

                for (documento in resultado.documents) {

                    val correo =
                        documento.getString("correo")
                            ?: documento.id

                    val nombre =
                        documento.getString("nombre")
                            ?: "Usuario"

                    val telefono =
                        documento.getString("telefono")
                            ?: ""

                    val rol =
                        documento.getString("rol")
                            ?: "buyer"

                    usuarios.add(
                        Usuario(
                            nombre,
                            correo,
                            telefono,
                            rol
                        )
                    )
                }

                usuarios.sortBy {
                    it.nombre.lowercase()
                }

                mostrarUsuarios(usuarios)
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "No fue posible cargar los usuarios",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    /**
     * Filtra los usuarios por nombre,
     * correo o rol.
     */
    private fun filtrarUsuarios() {

        val busqueda =
            etSearchUser.text
                .toString()
                .trim()
                .lowercase()

        if (busqueda.isEmpty()) {

            mostrarUsuarios(usuarios)
            return
        }

        val filtrados =
            usuarios.filter { usuario ->

                usuario.nombre
                    .lowercase()
                    .contains(busqueda) ||

                        usuario.correo
                            .lowercase()
                            .contains(busqueda) ||

                        usuario.rol
                            .lowercase()
                            .contains(busqueda)
            }

        mostrarUsuarios(filtrados)

        if (filtrados.isEmpty()) {

            Toast.makeText(
                this,
                getString(R.string.no_users_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Crea en pantalla una tarjeta
     * por cada usuario encontrado.
     */
    private fun mostrarUsuarios(
        lista: List<Usuario>
    ) {

        usersContainer.removeAllViews()

        for (usuario in lista) {

            usersContainer.addView(
                crearTarjetaUsuario(usuario)
            )
        }
    }

    /**
     * Construye una tarjeta sencilla con
     * los datos y botones del usuario.
     */
    private fun crearTarjetaUsuario(
        usuario: Usuario
    ): View {

        val espacioMd =
            resources.getDimensionPixelSize(
                R.dimen.space_md
            )

        val espacioSm =
            resources.getDimensionPixelSize(
                R.dimen.space_sm
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

        // Nombre
        val tvNombre =
            TextView(this).apply {

                text = usuario.nombre

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
            }

        card.addView(tvNombre)

        // Correo
        val tvCorreo =
            TextView(this).apply {

                text = usuario.correo

                setTextColor(
                    getColor(
                        R.color.text_secondary
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

        card.addView(tvCorreo)

        // Rol
        val textoRol =
            when (usuario.rol) {

                "buyer" ->
                    getString(R.string.buyer)

                "seller" ->
                    getString(R.string.seller)

                "admin" ->
                    getString(R.string.administrator)

                else ->
                    usuario.rol
            }

        val tvRol =
            TextView(this).apply {

                text = "Rol: $textoRol"

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

        card.addView(tvRol)

        // Teléfono, si existe
        if (usuario.telefono.isNotEmpty()) {

            val tvTelefono =
                TextView(this).apply {

                    text =
                        "Teléfono: ${usuario.telefono}"

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

            card.addView(tvTelefono)
        }

        // Fila de botones
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

        // Editar
        val btnEditar =
            AppCompatButton(this).apply {

                text =
                    getString(R.string.edit)

                isAllCaps = false

                setBackgroundResource(
                    R.drawable.bg_input
                )

                setTextColor(
                    getColor(
                        R.color.text_primary
                    )
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

                    abrirUsuario(
                        usuario.correo
                    )
                }
            }

        botones.addView(btnEditar)

        // Separación entre botones
        val espacio =
            View(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        espacioSm,
                        1
                    )
            }

        botones.addView(espacio)

        // Eliminar
        val btnEliminar =
            AppCompatButton(this).apply {

                text =
                    getString(R.string.delete)

                isAllCaps = false

                setBackgroundResource(
                    R.drawable.bg_input
                )

                setTextColor(
                    getColor(
                        R.color.error
                    )
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

                    confirmarEliminar(usuario)
                }
            }

        botones.addView(btnEliminar)

        card.addView(botones)

        return card
    }

    /**
     * Abre el formulario con la información
     * del usuario seleccionado.
     */
    private fun abrirUsuario(
        correo: String
    ) {

        val intent =
            Intent(
                this,
                UserFormActivity::class.java
            )

        intent.putExtra(
            "emailUsuario",
            correo
        )

        startActivity(intent)
    }

    /**
     * Solicita confirmación antes de eliminar
     * el usuario de Firestore.
     */
    private fun confirmarEliminar(
        usuario: Usuario
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                getString(R.string.delete_user)
            )
            .setMessage(
                getString(
                    R.string.delete_user_confirmation,
                    usuario.nombre
                )
            )
            .setNegativeButton(
                getString(R.string.cancel),
                null
            )
            .setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->

                db.collection("usuarios")
                    .document(usuario.correo)
                    .delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Usuario eliminado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarUsuarios()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "No fue posible eliminar el usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .show()
    }
}