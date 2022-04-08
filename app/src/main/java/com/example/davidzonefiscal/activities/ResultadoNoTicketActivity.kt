package com.example.davidzonefiscal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.davidzonefiscal.R

class ResultadoNoTicketActivity : AppCompatActivity() {

    private lateinit var btnIrregularidade : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_consulta_irregular)

        btnIrregularidade = findViewById(R.id.btnIrregularidade)
        btnIrregularidade.setOnClickListener{
            val intentRegistrar = Intent(this@ResultadoNoTicketActivity, SelecionarIrregularidadeActivity::class.java)
            startActivity(intentRegistrar)
        }
    }
}