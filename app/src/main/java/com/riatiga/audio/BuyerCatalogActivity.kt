package com.riatiga.audio

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BuyerCatalogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buyer_catalog)

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

        // Abrir detalle de RIATIGA Air Pro
        findViewById<LinearLayout>(R.id.cardAirPro).setOnClickListener {
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("producto", "air_pro")
            startActivity(intent)
        }

        // Abrir detalle de RIATIGA One
        findViewById<LinearLayout>(R.id.cardOne).setOnClickListener {
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("producto", "one")
            startActivity(intent)
        }

        // Volver al inicio del comprador
        findViewById<AppCompatButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}