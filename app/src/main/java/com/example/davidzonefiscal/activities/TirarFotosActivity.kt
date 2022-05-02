package com.example.davidzonefiscal.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.core.net.toUri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityTirarFotosBinding
import com.example.davidzonefiscal.entities.IrregularidadePhotosUpload
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TirarFotosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTirarFotosBinding

    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    val photos = arrayListOf<String>()

    val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                abrirPreview()
            }else{
                Toast.makeText(this, "Sem permissões para o uso da câmera.", Toast.LENGTH_SHORT).show()
            }
        }

    val getPhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.i("AAAAAAAAAAAAAAAAAAAA", result.data.toString())

        if(result.data == null) {
            Log.e("TirarFotosActivity", "Erro na foto recebida de CameraPreview")
            return@registerForActivityResult
        }

        val bundle = result.data!!.extras

        if (bundle != null && photos.size < 4) {
            val path = bundle.getString("path")

            if(path != null)
            {
                photos.add(path)
                return@registerForActivityResult
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTirarFotosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        functions = Firebase.functions("southamerica-east1")

        binding.btnStart.setOnClickListener {
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }
        getImages()
    }

    override fun onResume() {
        super.onResume()

        binding.tvCount.text = ("${photos.size}/4")

        when (photos.size) {
            1, 2, 3 -> binding.btnStart.text = ("Próximo")
            4 -> binding.btnStart.text = ("Enviar")
        }

        // colocar fotos recebidas nas imageViews
        if(photos.size > 0)
            binding.imgView1.setImageURI(photos[0].toUri())

        if (photos.size > 1)
            binding.imgView2.setImageURI(photos[1].toUri())

        if (photos.size > 2)
            binding.imgView3.setImageURI(photos[2].toUri())

        if (photos.size > 3)
            binding.imgView4.setImageURI(photos[3].toUri())
    }

    // função que recebe a ArrayList com as fotos tiradas
    private fun getImages() {

        // recebe os "extras" da activity anterior
        val bundle = intent.extras
        if (bundle != null) {
            val placa = bundle.getString("placa", "CU")
            binding.cdPlaca.text = placa
            val images = bundle.getStringArrayList("picturesFromPreview")
            if (images != null) {
                Log.i("CameraPreview", images.size.toString())
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
    }

    private fun abrirPreview() {
        val intentStart = Intent(this@TirarFotosActivity, CameraPreviewActivity::class.java)
        val bundle = intent.extras

        // enviar a ArrayList com as fotos para a proxima activity
        if (bundle != null ) {
            val placa = bundle.getString("placa", "CU")
            val tipo = bundle.getInt("tipo")
            val user = FirebaseAuth.getInstance().currentUser

            Log.i("aaaaaaaaaaaaaaaaaaaaaa", placa)

            // envia para tela de sucesso caso ja tenha tirado 4 fotos
            if ( photos.size >= 4 && placa != null && user != null ) {

                val uid = user.uid

                binding.btnStart.isClickable = false
                binding.btnStart.visibility = View.INVISIBLE

                binding.progressLoader.visibility = View.VISIBLE

                binding.tvCount.text = "Enviando fotos (0 de 4)"

                val uploadTransaction = IrregularidadePhotosUpload(photos)
                uploadTransaction.onProgressCallback = {finishedCounter ->
                    binding.tvCount.text = "Enviando fotos ($finishedCounter de 4)"
                }
                uploadTransaction.start { remotePaths ->
                    registrarIrregularidade(placa, tipo, remotePaths, uid)
                        .addOnCompleteListener(OnCompleteListener { task ->
                            Log.i("testeFunction", task.result.toString())

                            if (!task.isSuccessful) {
                                val e = task.exception
                                if (e is FirebaseFunctionsException) {
                                    val code = e.code
                                    val details = e.details
                                    Log.e("FirebaseFunctionsExc", "Error code $code : $details")
                                }
                                Log.w(TAG, "registrarIrregularidade:onFailure", e)
                                Snackbar.make(binding.btnStart, "Erro no servidor. Se o problema persistir ligue 0800-000-0000", Snackbar.LENGTH_LONG)
                                    .setAction("Tente novamente") {
                                        abrirPreview()
                                    }
                                    .show()

                                Log.i("testeFunction", task.result.toString())

                                binding.btnStart.visibility = View.VISIBLE
                                binding.btnStart.isClickable = true

                                binding.progressLoader.visibility = View.INVISIBLE
                            }
                            else {

                                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                                builder.setTitle(R.string.sucesso)
                                builder.setMessage(R.string.sucesso_registro_irregularidade)
                                builder.setPositiveButton(R.string.ok) { dialog, which ->
                                    finish()
                                }
                                val dialog: androidx.appcompat.app.AlertDialog = builder.create()
                                dialog.show()
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
                            }
                        })
                }

            } else {
                getPhoto.launch(intentStart)
                //startActivity(intentStart)
            }
        }
    }

    private fun registrarIrregularidade(placa: String, tipo: Number, images: ArrayList<String>, uid: String): Task<String> {
        val data = hashMapOf(
            "placa" to placa,
            "tipo" to tipo,
            "images" to images,
            "uid" to uid
        )
        return functions
            .getHttpsCallable("registrarIrregularidade")
            .call(data)
            .continueWith { task ->
                val result = gson.toJson(task.result?.data)
                result
            }
    }

}



