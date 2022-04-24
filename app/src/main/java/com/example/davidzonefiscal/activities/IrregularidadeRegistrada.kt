package com.example.davidzonefiscal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.davidzonefiscal.databinding.ActivityIrregularidadeRegistradaBinding

class IrregularidadeRegistrada : AppCompatActivity() {

    private lateinit var binding: ActivityIrregularidadeRegistradaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIrregularidadeRegistradaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}