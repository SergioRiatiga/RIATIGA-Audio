package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Pantalla de inicio de sesión.
 * Valida las credenciales con Firebase Authentication
 * y consulta el rol del usuario en Firestore.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Servicios de Firebase
        auth = FirebaseAuth.getInstance()
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

        val etEmail =
            findViewById<EditText>(R.id.etEmail)

        val etPassword =
            findViewById<EditText>(R.id.etPassword)

        val btnLogin =
            findViewById<AppCompatButton>(R.id.btnLogin)

        val btnBiometric =
            findViewById<AppCompatButton>(R.id.btnBiometric)

        val btnRegister =
            findViewById<AppCompatButton>(R.id.btnRegister)

        // Inicio de sesión con Firebase
        btnLogin.setOnClickListener {

            val email =
                etEmail.text.toString().trim().lowercase()

            val password =
                etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                mostrarCredencialesIncorrectas()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        consultarRol(email)

                    } else {

                        mostrarCredencialesIncorrectas()
                    }
                }
        }

        // Registro de nuevos usuarios.
        // Todo usuario registrado desde la aplicación se crea como comprador.
        btnRegister.setOnClickListener {

            val email =
                etEmail.text.toString().trim().lowercase()

            val password =
                etPassword.text.toString()

            if (email.isEmpty() || password.length < 6) {

                Toast.makeText(
                    this,
                    "Ingresa un correo válido y una contraseña de mínimo 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        val usuario = hashMapOf(
                            "nombre" to "Comprador",
                            "correo" to email,
                            "rol" to "buyer"
                        )

                        db.collection("usuarios")
                            .document(email)
                            .set(usuario)
                            .addOnSuccessListener {

                                Toast.makeText(
                                    this,
                                    "Cuenta creada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                guardarUltimoRol("buyer")
                                abrirPantallaSegunRol("buyer")
                            }
                            .addOnFailureListener {

                                Toast.makeText(
                                    this,
                                    "No fue posible guardar el usuario",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    } else {

                        Toast.makeText(
                            this,
                            "No fue posible crear la cuenta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // Inicio de sesión biométrico
        btnBiometric.setOnClickListener {
            iniciarAutenticacionBiometrica()
        }

        // Recuperación de contraseña
        findViewById<TextView>(R.id.tvForgotPassword)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        RecoverPasswordActivity::class.java
                    )
                )
            }
    }

    /**
     * Consulta el rol del usuario en Firestore.
     */
    private fun consultarRol(email: String) {

        db.collection("usuarios")
            .document(email)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    val rol =
                        documento.getString("rol")

                    if (rol != null) {

                        guardarUltimoRol(rol)
                        abrirPantallaSegunRol(rol)

                    } else {

                        mostrarUsuarioSinRol()
                    }

                } else {

                    mostrarUsuarioSinRol()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "No fue posible consultar el usuario",
                    Toast.LENGTH_SHORT
                ).show()

                auth.signOut()
            }
    }

    /**
     * Comprueba la disponibilidad de autenticación biométrica.
     */
    private fun iniciarAutenticacionBiometrica() {

        val preferencias =
            getSharedPreferences(
                "riatiga_preferences",
                MODE_PRIVATE
            )

        val ultimoRol =
            preferencias.getString(
                "ultimo_rol",
                null
            )

        if (ultimoRol == null) {

            Toast.makeText(
                this,
                getString(R.string.login_first),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val biometricManager =
            BiometricManager.from(this)

        when (
            biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
        ) {

            BiometricManager.BIOMETRIC_SUCCESS -> {
                mostrarDialogoBiometrico(ultimoRol)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(
                    this,
                    getString(R.string.biometric_no_hardware),
                    Toast.LENGTH_SHORT
                ).show()
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(
                    this,
                    getString(R.string.biometric_unavailable),
                    Toast.LENGTH_SHORT
                ).show()
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(
                    this,
                    getString(R.string.biometric_not_enrolled),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                Toast.makeText(
                    this,
                    getString(R.string.biometric_not_available),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Muestra la ventana de autenticación biométrica.
     */
    private fun mostrarDialogoBiometrico(rol: String) {

        val executor =
            ContextCompat.getMainExecutor(this)

        val biometricPrompt =
            BiometricPrompt(
                this,
                executor,
                object :
                    BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.biometric_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        abrirPantallaSegunRol(rol)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.biometric_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(
                    getString(R.string.biometric_title)
                )
                .setSubtitle(
                    getString(R.string.biometric_subtitle)
                )
                .setNegativeButtonText(
                    getString(R.string.cancel)
                )
                .build()

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Guarda el último rol utilizado para el acceso biométrico.
     */
    private fun guardarUltimoRol(rol: String) {

        val preferencias =
            getSharedPreferences(
                "riatiga_preferences",
                MODE_PRIVATE
            )

        preferencias.edit()
            .putString("ultimo_rol", rol)
            .apply()
    }

    /**
     * Abre la pantalla correspondiente al rol del usuario.
     */
    private fun abrirPantallaSegunRol(rol: String) {

        when (rol) {

            "buyer" -> {
                startActivity(
                    Intent(
                        this,
                        BuyerHomeActivity::class.java
                    )
                )
            }

            "seller" -> {
                startActivity(
                    Intent(
                        this,
                        SellerDashboardActivity::class.java
                    )
                )
            }

            "admin" -> {
                startActivity(
                    Intent(
                        this,
                        AdminDashboardActivity::class.java
                    )
                )
            }

            else -> {
                mostrarUsuarioSinRol()
                return
            }
        }

        finish()
    }

    /**
     * Muestra un mensaje cuando las credenciales son incorrectas.
     */
    private fun mostrarCredencialesIncorrectas() {

        Toast.makeText(
            this,
            getString(R.string.invalid_credentials),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Muestra un mensaje cuando el usuario no tiene un rol en Firestore.
     */
    private fun mostrarUsuarioSinRol() {

        Toast.makeText(
            this,
            "Usuario sin rol asignado",
            Toast.LENGTH_SHORT
        ).show()

        auth.signOut()
    }
}