package com.example.davidzonefiscal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityTirarFotosBinding

class TirarFotosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTirarFotosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTirarFotosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener{
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun abrirPreview() {
        val intentStart = Intent(this@TirarFotosActivity, CameraPreviewActivity::class.java)
        startActivity(intentStart)
    }

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                abrirPreview()
            }else{
                Toast.makeText(this, "Sem permissões para o uso da câmera.", Toast.LENGTH_SHORT).show()
            }
        }
}