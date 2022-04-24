package com.example.davidzonefiscal.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityTirarFotosBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TirarFotosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTirarFotosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTirarFotosBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnStart.setOnClickListener {
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }

        getImages()
    }

    // função que recebe a ArrayList com as fotos tiradas
    private fun getImages() {

        // recebe os "extras" da activity anterior
        val bundle = intent.extras
        if (bundle != null) {
            val images = bundle.getStringArrayList("picture1")

            // mostrar progresso
            binding.tvCount.text = ("${images?.size}/4")

            // condicional para mudar texto do botão principal
            when (images?.size) {
                1, 2, 3 -> binding.btnStart.text = ("Próximo")
                4 -> binding.btnStart.text = ("Enviar")
            }

            // colocar fotos recebidas nas imageViews
            binding.imgView1.setImageURI(images?.get(0)?.toUri())
            if (images?.size!! >= 2) {
                binding.imgView2.setImageURI(images?.get(1)?.toUri())
            }
            if (images?.size!! >= 3) {
                binding.imgView3.setImageURI(images?.get(2)?.toUri())
            }
            if (images?.size == 4) {
                binding.imgView4.setImageURI(images?.get(3)?.toUri())
            }
        }
    }

    private fun abrirPreview() {
        val intentStart = Intent(this@TirarFotosActivity, CameraPreviewActivity::class.java)
        val bundle = intent.extras

        // enviar a ArrayList com as fotos para a proxima activity
        if (bundle != null ) {
            intentStart.putExtra("pictures", bundle.getStringArrayList("picture1"))
        }

        // envia para tela de sucesso caso ja tenha tirado 4 fotos
        if ( bundle?.getStringArrayList("picture1")?.size == 4 ) {
            val intentSend = Intent(this, IrregularidadeRegistrada::class.java)
            startActivity(intentSend)
        } else { startActivity(intentStart) }
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

     