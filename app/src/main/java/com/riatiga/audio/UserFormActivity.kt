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

        val tvTitle = findViewById<TextView>(R.id.tvUserFormTitle)

        val etName = findViewById<EditText>(R.id.etUserName)
        val etEmail = findViewById<EditText>(R.id.etUserEmail)
        val etPhone = findViewById<EditText>(R.id.etUserPhone)
        val etPassword = findViewById<EditText>(R.id.etUserPassword)
        val spinnerRole = findViewById<Spinner>(R.id.spinnerUserRole)

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

        // Recibir usuario cuando se abre la pantalla para editar
        usuarioSeleccionado = intent.getStringExtra("usuario")

        when (usuarioSeleccionado) {

            "buyer" -> {
                tvTitle.text = getString(R.string.edit_user)

                etName.setText(getString(R.string.buyer_demo_name))
                etEmail.setText(getString(R.string.buyer_demo_email))
                etPhone.setText(getString(R.string.buyer_demo_phone))
                etPassword.setText(getString(R.string.demo_password))

                spinnerRole.setSelection(0)
            }

            "seller" -> {
                tvTitle.text = getString(R.string.edit_user)

                etName.setText(getString(R.string.seller_demo_name))
                etEmail.setText(getString(R.string.seller_demo_email))
                etPhone.setText(getString(R.string.seller_demo_phone))
                etPassword.setText(getString(R.string.demo_password))

                spinnerRole.setSelection(1)
            }

            "admin" -> {
                tvTitle.text = getString(R.string.edit_user)

                etName.setText(getString(R.string.admin_demo_name))
                etEmail.setText(getString(R.string.admin_demo_email))
                etPhone.setText(getString(R.string.admin_demo_phone))
                etPassword.setText(getString(R.string.demo_password))

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
                    getString(R.string.complete_all_fields),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {

                Toast.makeText(
                    this,
                    getString(R.string.invalid_email),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (usuarioSeleccionado == null) {

                Toast.makeText(
                    this,
                    getString(
                        R.string.user_created,
                        rol
                    ),
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    getString(R.string.user_updated),
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