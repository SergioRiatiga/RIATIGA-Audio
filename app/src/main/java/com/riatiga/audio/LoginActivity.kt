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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

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

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<AppCompatButton>(R.id.btnLogin)
        val btnBiometric = findViewById<AppCompatButton>(R.id.btnBiometric)

        // Inicio de sesión normal según las credenciales y el rol
        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim().lowercase()
            val password = etPassword.text.toString()

            if (password != "1234") {
                mostrarCredencialesIncorrectas()
                return@setOnClickListener
            }

            when (email) {

                "comprador@riatiga.com" -> {
                    guardarUltimoRol("buyer")
                    abrirPantallaSegunRol("buyer")
                }

                "vendedor@riatiga.com" -> {
                    guardarUltimoRol("seller")
                    abrirPantallaSegunRol("seller")
                }

                "admin@riatiga.com" -> {
                    guardarUltimoRol("admin")
                    abrirPantallaSegunRol("admin")
                }

                else -> {
                    mostrarCredencialesIncorrectas()
                }
            }
        }

        // Inicio de sesión biométrico
        btnBiometric.setOnClickListener {
            iniciarAutenticacionBiometrica()
        }

        // Recuperación de contraseña
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RecoverPasswordActivity::class.java
                )
            )
        }
    }

    private fun iniciarAutenticacionBiometrica() {

        val preferencias =
            getSharedPreferences("riatiga_preferences", MODE_PRIVATE)

        val ultimoRol =
            preferencias.getString("ultimo_rol", null)

        // Primero debe existir un inicio de sesión normal
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

    private fun mostrarDialogoBiometrico(rol: String) {

        val executor =
            ContextCompat.getMainExecutor(this)

        val biometricPrompt =
            BiometricPrompt(
                this,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {

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

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(
                            errorCode,
                            errString
                        )
                    }
                }
            )

        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.cancel))
                .build()

        biometricPrompt.authenticate(promptInfo)
    }

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
        }
    }

    private fun mostrarCredencialesIncorrectas() {

        Toast.makeText(
            this,
            getString(R.string.invalid_credentials),
            Toast.LENGTH_SHORT
        ).show()
    }
}