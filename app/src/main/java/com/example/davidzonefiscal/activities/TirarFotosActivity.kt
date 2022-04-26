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
import com.example.davidzonefiscal.entities.IrregularidadePhotosUpload
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TirarFotosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTirarFotosBinding

    private lateinit var storageRef: StorageReference

    private var imagePaths: Array<Uri> = arrayOf(Uri.EMPTY, Uri.EMPTY, Uri.EMPTY, Uri.EMPTY)

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
            imagePaths[0] = images?.get(0)?.toUri() as Uri
            if (images?.size!! >= 2) {
                binding.imgView2.setImageURI(images?.get(1)?.toUri())
                imagePaths[1] = images?.get(1)?.toUri() as Uri
            }
            if (images?.size!! >= 3) {
                binding.imgView3.setImageURI(images?.get(2)?.toUri())
                imagePaths[2] = images?.get(2)?.toUri() as Uri
            }
            if (images?.size == 4) {
                binding.imgView4.setImageURI(images?.get(3)?.toUri())
                imagePaths[3] = images?.get(3)?.toUri() as Uri
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
            val uploader = IrregularidadePhotosUpload(imagePaths)
            val uploadTask = uploader.upload()
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
                startActivity(intentSend)
            }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // If there's a download in progress, save the reference so you can query it later
        outState.putString("reference", storageRef.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // If there was a download in progress, get its reference and create a new StorageReference
        val stringRef = savedInstanceState.getString("reference") ?: return

        storageRef = Firebase.storage.getReferenceFromUrl(stringRef)

        // Find all DownloadTasks under this StorageReference (in this example, there should be one)
        val tasks = storageRef.activeDownloadTasks

        if (tasks.size > 0) {
            // Get the task monitoring the download
            val task = tasks[0]

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this) {
                // Success!
                // ...
            }
        }
    }

}

     