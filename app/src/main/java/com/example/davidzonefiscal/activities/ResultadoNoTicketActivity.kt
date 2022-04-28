package com.example.davidzonefiscal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityResultadoConsultaIrregularBinding

class ResultadoNoTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultadoConsultaIrregularBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultadoConsultaIrregularBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIrregularidade.setOnClickListener{
            val intentRegistrar = Intent(this@ResultadoNoTicketActivity, SelecionarIrregularidadeActivity::class.java)
            startActivity(intentRegistrar)
        }
    }
}