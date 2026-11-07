package com.riatiga.audio

import android.os.Bundle
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

class UserFormActivity : AppCompatActivity() {

    private var usuarioSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_form)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val tvTitle = findViewById<TextView>(R.id.tvUserFormTitle)

        val etName = findViewById<EditText>(R.id.etUserName)
        val etEmail = findViewById<EditText>(R.id.etUserEmail)
        val etPhone = findViewById<EditText>(R.id.etUserPhone)
        val etPassword = findViewById<EditText>(R.id.etUserPassword)
        val spinnerRole = findViewById<Spinner>(R.id.spinnerUserRole)

        // Roles disponibles
        val roles = arrayOf(
            "Comprador",
            "Vendedor",
            "Administrador"
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

        // Recibir usuario cuando se abre la pantalla para editar
        usuarioSeleccionado = intent.getStringExtra("usuario")

        when (usuarioSeleccionado) {

            "buyer" -> {
                tvTitle.text = "Editar usuario"

                etName.setText("Sergio Riatiga")
                etEmail.setText("comprador@riatiga.com")
                etPhone.setText("300 123 4567")
                etPassword.setText("1234")

                spinnerRole.setSelection(0)
            }

            "seller" -> {
                tvTitle.text = "Editar usuario"

                etName.setText("Vendedor RIATIGA")
                etEmail.setText("vendedor@riatiga.com")
                etPhone.setText("300 555 1234")
                etPassword.setText("1234")

                spinnerRole.setSelection(1)
            }

            "admin" -> {
                tvTitle.text = "Editar usuario"

                etName.setText("Administrador RIATIGA")
                etEmail.setText("admin@riatiga.com")
                etPhone.setText("300 777 1234")
                etPassword.setText("1234")

                spinnerRole.setSelection(2)
            }

            else -> {
                // Nuevo usuario
                tvTitle.text = getString(R.string.new_user)
                spinnerRole.setSelection(0)
            }
        }

        // Guardar usuario
        findViewById<AppCompatButton>(R.id.btnSaveUser).setOnClickListener {

            val nombre = etName.text.toString().trim()
            val correo = etEmail.text.toString().trim()
            val telefono = etPhone.text.toString().trim()
            val password = etPassword.text.toString()
            val rol = spinnerRole.selectedItem.toString()

            if (
                nombre.isEmpty() ||
                correo.isEmpty() ||
                telefono.isEmpty() ||
                password.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {

                Toast.makeText(
                    this,
                    "Ingresa un correo electrónico válido",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (usuarioSeleccionado == null) {

                Toast.makeText(
                    this,
                    "Usuario creado como $rol",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Usuario actualizado correctamente",
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