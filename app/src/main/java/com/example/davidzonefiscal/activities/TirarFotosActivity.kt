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
    private lateinit var image1: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTirarFotosBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnStart.setOnClickListener {
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }

        getImages()
    }

    private fun getImages() {
        val bundle = intent.extras
        if (bundle != null) {
            image1 = bundle.getString("picture1").toString()
            binding.imgView1.setImageURI(image1.toUri())
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

     