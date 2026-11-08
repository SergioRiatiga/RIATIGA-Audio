package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UserManagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_management)

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

        val etSearchUser = findViewById<EditText>(R.id.etSearchUser)

        val cardBuyer = findViewById<LinearLayout>(R.id.cardBuyerUser)
        val cardSeller = findViewById<LinearLayout>(R.id.cardSellerUser)
        val cardAdmin = findViewById<LinearLayout>(R.id.cardAdminUser)

        // Crear nuevo usuario
        findViewById<AppCompatButton>(R.id.btnNewUser).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    UserFormActivity::class.java
                )
            )
        }

        // Editar comprador
        findViewById<AppCompatButton>(R.id.btnEditBuyer).setOnClickListener {

            val intent = Intent(
                this,
                UserFormActivity::class.java
            )

            intent.putExtra("usuario", "buyer")
            startActivity(intent)
        }

        // Editar vendedor
        findViewById<AppCompatButton>(R.id.btnEditSeller).setOnClickListener {

            val intent = Intent(
                this,
                UserFormActivity::class.java
            )

            intent.putExtra("usuario", "seller")
            startActivity(intent)
        }

        // Editar administrador
        findViewById<AppCompatButton>(R.id.btnEditAdmin).setOnClickListener {

            val intent = Intent(
                this,
                UserFormActivity::class.java
            )

            intent.putExtra("usuario", "admin")
            startActivity(intent)
        }

        // Eliminar comprador
        findViewById<AppCompatButton>(R.id.btnDeleteBuyer).setOnClickListener {

            mostrarConfirmacionEliminar(
                getString(R.string.buyer_demo_name),
                cardBuyer
            )
        }

        // Eliminar vendedor
        findViewById<AppCompatButton>(R.id.btnDeleteSeller).setOnClickListener {

            mostrarConfirmacionEliminar(
                getString(R.string.seller_demo_name),
                cardSeller
            )
        }

        // Eliminar administrador
        findViewById<AppCompatButton>(R.id.btnDeleteAdmin).setOnClickListener {

            mostrarConfirmacionEliminar(
                getString(R.string.admin_demo_name),
                cardAdmin
            )
        }

        // Buscar / filtrar usuarios
        findViewById<AppCompatButton>(R.id.btnFilterUsers).setOnClickListener {

            val busqueda =
                etSearchUser.text.toString().trim().lowercase()

            if (busqueda.isEmpty()) {

                cardBuyer.visibility = View.VISIBLE
                cardSeller.visibility = View.VISIBLE
                cardAdmin.visibility = View.VISIBLE

                Toast.makeText(
                    this,
                    getString(R.string.showing_all_users),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val mostrarBuyer =
                getString(R.string.buyer_demo_name)
                    .lowercase()
                    .contains(busqueda) ||
                        getString(R.string.buyer_demo_email)
                            .lowercase()
                            .contains(busqueda) ||
                        getString(R.string.buyer)
                            .lowercase()
                            .contains(busqueda)

            val mostrarSeller =
                getString(R.string.seller_demo_name)
                    .lowercase()
                    .contains(busqueda) ||
                        getString(R.string.seller_demo_email)
                            .lowercase()
                            .contains(busqueda) ||
                        getString(R.string.seller)
                            .lowercase()
                            .contains(busqueda)

            val mostrarAdmin =
                getString(R.string.admin_demo_name)
                    .lowercase()
                    .contains(busqueda) ||
                        getString(R.string.admin_demo_email)
                            .lowercase()
                            .contains(busqueda) ||
                        getString(R.string.administrator)
                            .lowercase()
                            .contains(busqueda)

            cardBuyer.visibility =
                if (mostrarBuyer) View.VISIBLE else View.GONE

            cardSeller.visibility =
                if (mostrarSeller) View.VISIBLE else View.GONE

            cardAdmin.visibility =
                if (mostrarAdmin) View.VISIBLE else View.GONE

            if (!mostrarBuyer && !mostrarSeller && !mostrarAdmin) {

                Toast.makeText(
                    this,
                    getString(R.string.no_users_found),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Volver
        findViewById<AppCompatButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun mostrarConfirmacionEliminar(
        nombreUsuario: String,
        tarjetaUsuario: View
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                getString(R.string.delete_user)
            )
            .setMessage(
                getString(
                    R.string.delete_user_confirmation,
                    nombreUsuario
                )
            )
            .setNegativeButton(
                getString(R.string.cancel),
                null
            )
            .setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->

                tarjetaUsuario.visibility = View.GONE
            }
            .show()
    }
}