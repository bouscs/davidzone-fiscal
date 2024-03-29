package com.example.davidzonefiscal.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.davidzonefiscal.databinding.ActivityCameraPreviewBinding
import com.example.davidzonefiscal.entities.DavidGlobals
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class CameraPreviewActivity : AppCompatActivity() {

    var globals: DavidGlobals = DavidGlobals()

    private lateinit var binding: ActivityCameraPreviewBinding
    // Processamento de imagem
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    // Selecionar camera frontal ou traseira
    private lateinit var cameraSelector: CameraSelector

    // Imagem Capturada
    private var imageCapture:ImageCapture? = null

    // Executor de thread separada
    private lateinit var imgCaptureExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        // chamar o startCamera()
        startCamera()

        binding.abTirarFoto.setOnClickListener{

            globals.timerbotaoFAB(binding.abTirarFoto)
            takePhoto()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                blinkPreview()
            }
        }
    }

    private fun startCamera(){
        cameraProviderFuture.addListener({

            imageCapture = ImageCapture.Builder().build()

            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception){
                Log.e("CameraPreview", "Falha ao abrir a camera.")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        imageCapture?.let{
            //nome do arquivo para gravar a foto
            val fileName = "FOTO_JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)
            val intentSuccess = Intent(this, TirarFotosActivity::class.java)

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object: ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i("CameraPreview", "A imagem foi salva no diretório: ${file.toUri()}")


                            // envia ArrayList para proxima activity
                            //startActivity(intentSuccess)
                            setResult(0, Intent().putExtra("path", file.toString()))
                            finish()

                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(binding.root.context, "Erro ao salvar foto.", Toast.LENGTH_SHORT).show()
                        Log.e("CameraPreview", "Exceção ao gravar arquivo da foto: $exception")
                    }
                })
        }
    }

    // importar ArrayList contendo as fotos já salvas, caso nao exista cria uma ArrayList vazia
    private fun importArrayList(): ArrayList<String>? {
        val bundle = intent.extras
        if (bundle != null) {
            return bundle.getStringArrayList("picturesToPreview")
        } else {
            return ArrayList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun blinkPreview(){
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }
}