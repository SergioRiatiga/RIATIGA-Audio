package com.riatiga.audio

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Formulario utilizado por el administrador para
 * crear y editar usuarios almacenados en Firestore.
 */
class UserFormActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var emailSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_form)

        db = FirebaseFirestore.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val tvTitle =
            findViewById<TextView>(R.id.tvUserFormTitle)

        val etName =
            findViewById<EditText>(R.id.etUserName)

        val etEmail =
            findViewById<EditText>(R.id.etUserEmail)

        val etPhone =
            findViewById<EditText>(R.id.etUserPhone)

        val etPassword =
            findViewById<EditText>(R.id.etUserPassword)

        val spinnerRole =
            findViewById<Spinner>(R.id.spinnerUserRole)

        /*
         * La contraseña se administra mediante Firebase Authentication.
         * Por seguridad no se guarda dentro de Firestore.
         */
        etPassword.visibility = View.GONE

        // Roles disponibles
        val roles = arrayOf(
            getString(R.string.buyer),
            getString(R.string.seller),
            getString(R.string.administrator)
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerRole.adapter = adapter

        // Si llega un correo, el formulario está editando un usuario.
        emailSeleccionado =
            intent.getStringExtra("emailUsuario")

        if (emailSeleccionado != null) {

            tvTitle.text =
                getString(R.string.edit_user)

            etEmail.setText(emailSeleccionado)

            // El correo se usa como ID del documento.
            // Para mantener el CRUD sencillo no se modifica al editar.
            etEmail.isEnabled = false

            cargarUsuario(
                emailSeleccionado!!,
                etName,
                etPhone,
                spinnerRole
            )

        } else {

            tvTitle.text =
                getString(R.string.new_user)

            spinnerRole.setSelection(0)
        }

        // Guardar usuario en Firestore
        findViewById<AppCompatButton>(
            R.id.btnSaveUser
        ).setOnClickListener {

            val nombre =
                etName.text.toString().trim()

            val correo =
                etEmail.text.toString()
                    .trim()
                    .lowercase()

            val telefono =
                etPhone.text.toString().trim()

            if (
                nombre.isEmpty() ||
                correo.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Nombre y correo son obligatorios",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(correo)
                    .matches()
            ) {

                Toast.makeText(
                    this,
                    getString(R.string.invalid_email),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Convierte el texto del Spinner al valor usado por la aplicación.
            val rol = when (spinnerRole.selectedItemPosition) {

                0 -> "buyer"
                1 -> "seller"
                2 -> "admin"

                else -> "buyer"
            }

            val usuario = hashMapOf(
                "nombre" to nombre,
                "correo" to correo,
                "telefono" to telefono,
                "rol" to rol
            )

            val documento =
                emailSeleccionado ?: correo

            db.collection("usuarios")
                .document(documento)
                .set(usuario)
                .addOnSuccessListener {

                    val mensaje =
                        if (emailSeleccionado == null)
                            "Usuario creado correctamente"
                        else
                            "Usuario actualizado correctamente"

                    Toast.makeText(
                        this,
                        mensaje,
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "No fue posible guardar el usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        // Cancelar
        findViewById<AppCompatButton>(
            R.id.btnCancel
        ).setOnClickListener {

            finish()
        }
    }

    /**
     * Consulta en Firestore la información del usuario
     * seleccionado por el administrador.
     */
    private fun cargarUsuario(
        email: String,
        etName: EditText,
        etPhone: EditText,
        spinnerRole: Spinner
    ) {

        db.collection("usuarios")
            .document(email)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    etName.setText(
                        documento.getString("nombre") ?: ""
                    )

                    etPhone.setText(
                        documento.getString("telefono") ?: ""
                    )

                    when (documento.getString("rol")) {

                        "buyer" ->
                            spinnerRole.setSelection(0)

                        "seller" ->
                            spinnerRole.setSelection(1)

                        "admin" ->
                            spinnerRole.setSelection(2)
                    }
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "No fue posible cargar el usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}