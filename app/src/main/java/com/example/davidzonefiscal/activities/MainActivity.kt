package com.example.davidzonefiscal.activities

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.davidzonefiscal.R
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.davidzonefiscal.databinding.ActivityMainBinding
import com.example.davidzonefiscal.databinding.ActivityMapsBinding
import com.example.davidzonefiscal.entities.GetItinerarioResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private val logEntry = "MAPS_ITINERARIO";

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        functions = Firebase.functions("southamerica-east1")

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        } else {
            startMapActivity()
        }
    }

    fun startMapActivity(){
        val slug = "itinerario1"

        getItinerario (slug)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        val code = e.code
                        val details = e.details
                    }
                    Log.w(logEntry, "getItinerario:onFailure", e)
                    Snackbar.make(binding.root, "Erro no servidor. Se o problema persistir ligue 0800-000-0000", Snackbar.LENGTH_LONG).show()
                    return@OnCompleteListener
                }

                val result = task.result
                val getItinerarioRes = gson.fromJson(result, GetItinerarioResponse::class.java)



                val intinerarioResult = getItinerarioRes.result.payload

                val logradouro1 = intinerarioResult.itinerario.logradouros[0]
                val logradouro1Nome = logradouro1.nome

                val logradouro1Ponto1Lng = logradouro1.pontos[0]._longitude.toString()
                val logradouro1Ponto1Lat = logradouro1.pontos[0]._latitude.toString()


                val logradouro1Ponto2Lng = logradouro1.pontos[1]._longitude.toString()
                val logradouro1Ponto2Lat = logradouro1.pontos[1]._latitude.toString()

                val logradouro1Ponto3Lng = logradouro1.pontos[2]._longitude.toString()
                val logradouro1Ponto3Lat = logradouro1.pontos[2]._latitude.toString()


                val logradouro2 = intinerarioResult.itinerario.logradouros[1]
                val logradouro2Nome = logradouro2.nome

                val logradouro2Ponto1Lng = logradouro2.pontos[0]._longitude.toString()
                val logradouro2Ponto1Lat = logradouro2.pontos[0]._latitude.toString()

                val logradouro2Ponto2Lng = logradouro2.pontos[1]._longitude.toString()
                val logradouro2Ponto2Lat = logradouro2.pontos[1]._latitude.toString()

                val logradouro2Ponto3Lng = logradouro2.pontos[2]._longitude.toString()
                val logradouro2Ponto3Lat = logradouro2.pontos[2]._latitude.toString()


                val logradouro3 = intinerarioResult.itinerario.logradouros[2]
                val logradouro3Nome = logradouro3.nome

                val logradouro3Ponto1Lng = logradouro3.pontos[0]._longitude.toString()
                val logradouro3Ponto1Lat = logradouro3.pontos[0]._latitude.toString()

                val logradouro3Ponto2Lng = logradouro3.pontos[1]._longitude.toString()
                val logradouro3Ponto2Lat = logradouro3.pontos[1]._latitude.toString()

                val logradouro3Ponto3Lng = logradouro3.pontos[2]._longitude.toString()
                val logradouro3Ponto3Lat = logradouro3.pontos[2]._latitude.toString()

                // Log.i(logEntry, logradouro1.pontos[0].toString() )
                // Log.i(logEntry, logradouro1.pontos[1].toString() )
                // Log.i(logEntry, logradouro1.pontos[2].toString() )


                Handler().postDelayed({
                    val intent = Intent(this@MainActivity, MapsActivity::class.java)

                    intent.putExtra("logradouro1Nome",logradouro1Nome)

                    intent.putExtra("logradouro1Ponto1Lng",logradouro1Ponto1Lng)
                    intent.putExtra("logradouro1Ponto1Lat",logradouro1Ponto1Lat)

                    intent.putExtra("logradouro1Ponto2Lng",logradouro1Ponto2Lng)
                    intent.putExtra("logradouro1Ponto2Lat",logradouro1Ponto2Lat)

                    intent.putExtra("logradouro1Ponto3Lng",logradouro1Ponto3Lng)
                    intent.putExtra("logradouro1Ponto3Lat",logradouro1Ponto3Lat)


                    intent.putExtra("logradouro2Nome",logradouro2Nome)

                    intent.putExtra("logradouro2Ponto1Lng",logradouro2Ponto1Lng)
                    intent.putExtra("logradouro2Ponto1Lat",logradouro2Ponto1Lat)

                    intent.putExtra("logradouro2Ponto2Lng",logradouro2Ponto2Lng)
                    intent.putExtra("logradouro2Ponto2Lat",logradouro2Ponto2Lat)

                    intent.putExtra("logradouro2Ponto3Lng",logradouro2Ponto3Lng)
                    intent.putExtra("logradouro2Ponto3Lat",logradouro2Ponto3Lat)


                    intent.putExtra("logradouro3Nome",logradouro3Nome)

                    intent.putExtra("logradouro3Ponto1Lng",logradouro3Ponto1Lng)
                    intent.putExtra("logradouro3Ponto1Lat",logradouro3Ponto1Lat)

                    intent.putExtra("logradouro3Ponto2Lng",logradouro3Ponto2Lng)
                    intent.putExtra("logradouro3Ponto2Lat",logradouro3Ponto2Lat)

                    intent.putExtra("logradouro3Ponto3Lng",logradouro3Ponto3Lng)
                    intent.putExtra("logradouro3Ponto3Lat",logradouro3Ponto3Lat)

                    startActivity(intent)
                    finish()
                }, 1000)

                return@OnCompleteListener
            })
    }


    private fun getItinerario (slug: String): Task<String> {
        val data = hashMapOf(
            "slug" to slug
        )
        return functions
            .getHttpsCallable("getItinerario")
            .call(data)
            .continueWith { task ->
                val result = gson.toJson(task.result?.data)
                result
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        startMapActivity()
                    }
                } else {
                    Toast.makeText(this, "O usuário não permitiu o uso do GPS!", Toast.LENGTH_SHORT).show()
                    binding.tvErro.visibility = View.VISIBLE
                }
                return
            }
        }
    }
}