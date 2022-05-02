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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityTirarFotosBinding
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



    // função que recebe a ArrayList com as fotos tiradas
    private fun getImages() {

        // recebe os "extras" da activity anterior
        val bundle = intent.extras
        if (bundle != null) {
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
        Log.i("testeFunction", "abrirPreview")
        val intentStart = Intent(this@TirarFotosActivity, CameraPreviewActivity::class.java)
        val bundle = intent.extras


        // enviar a ArrayList com as fotos para a proxima activity
        if (bundle != null ) {
            val placa = bundle.getString("placa")
            val tipo = bundle.getInt("tipo")
            Log.i("testeFunction", placa.toString())
            Log.i("testeFunction", tipo.toString())

            var picturesFromPreview = bundle.getStringArrayList("picturesFromPreview")
            if (picturesFromPreview == null) picturesFromPreview = arrayListOf()

            intentStart.putExtra("picturesToPreview", picturesFromPreview)
            intentStart.putExtra("placa", placa)
            intentStart.putExtra("tipo", tipo)

            // envia para tela de sucesso caso ja tenha tirado 4 fotos
            if ( bundle.getStringArrayList("picturesFromPreview")?.size == 4 ) {

                val images = bundle.getStringArrayList("picturesFromPreview")
                val uid = FirebaseAuth.getInstance().currentUser.toString()
                Log.i("testeFunction", images.toString())
                Log.i("testeFunction", uid)
                if (images != null && placa != null) {
                    registrarIrregularidade(placa, tipo, images, uid)
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
                                return@OnCompleteListener
                            }
                            else {
                                Log.i("testeFunction", task.result.toString())
                            }
                        })
                }


                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                builder.setTitle(R.string.sucesso)
                builder.setMessage(R.string.sucesso_registro_irregularidade)
                builder.setPositiveButton(R.string.ok) { dialog, which ->
                    val intentSend = Intent(this, MapsActivity::class.java)
                    startActivity(intentSend)
                }
                val dialog: androidx.appcompat.app.AlertDialog = builder.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)

            } else { startActivity(intentStart) }
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

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                abrirPreview()
            }else{
                Toast.makeText(this, "Sem permissões para o uso da câmera.", Toast.LENGTH_SHORT).show()
            }
        }
    }


