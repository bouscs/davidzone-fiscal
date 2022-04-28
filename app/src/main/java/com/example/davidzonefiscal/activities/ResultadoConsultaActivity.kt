package com.example.davidzonefiscal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityResultadoConsultaRegularBinding

class ResultadoConsultaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultadoConsultaRegularBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultadoConsultaRegularBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var placa: String? = intent.getStringExtra("consultaData")

        binding.etPlaca.text = placa?.uppercase()
    }
}