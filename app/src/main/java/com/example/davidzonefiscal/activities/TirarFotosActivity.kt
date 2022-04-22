package com.example.davidzonefiscal.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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

class TirarFotosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTirarFotosBinding
    private var i = 0
    private lateinit var photoFile: File
    lateinit var currentPhotoPath: String
    private val pictureFromCamera: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTirarFotosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        binding.btnStart.setOnClickListener {
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }*/

        binding.btnStart.setOnClickListener {
            takePicture()
        }
    }


    private fun takePicture(){
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        val uri = FileProvider.getUriForFile(this, "com.example.davidzonefiscal.fileprovider", photoFile)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(pictureIntent, pictureFromCamera)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == pictureFromCamera) {
                val uri = FileProvider.getUriForFile(
                    this,
                    "com.example.davidzonefiscal.fileprovider",
                    photoFile
                )
                i++
                when (i) {
                    1 -> {
                        binding.imgView1!!.setImageURI(uri)
                        binding.tvCount.text = "1/4"
                        binding.btnStart.text = "Próximo"
                    }
                    2 -> {
                        binding.imgView2!!.setImageURI(uri)
                        binding.tvCount.text = "2/4"
                    }
                    3 -> {
                        binding.imgView3!!.setImageURI(uri)
                        binding.tvCount.text = "3/4"
                    }
                    4 -> {
                        binding.imgView4!!.setImageURI(uri)
                        binding.tvCount.text = "4/4"
                        binding.btnStart.text = "Enviar"
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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

     