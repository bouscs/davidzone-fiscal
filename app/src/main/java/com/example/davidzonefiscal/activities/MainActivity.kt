package com.example.davidzonefiscal.activities

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityMainBinding
import com.example.davidzonefiscal.entities.DavidGlobals
import com.example.davidzonefiscal.entities.GetItinerarioResponse
import com.example.davidzonefiscal.entities.Itinerario
import com.example.davidzonefiscal.entities.Logradouro
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import java.util.*


private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private val logEntry = "MAIN_ACTIVITY";

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        functions = Firebase.functions("southamerica-east1")
        auth = Firebase.auth

        //cria usuario anonimo no firebase
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInAnonymously:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }


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
        createFiscalFirestore()
    }

    fun startMapActivity(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        var slug = "itinerario1"
        if (uid != null) {
            if(uid[0].isLetter()) {
                var slug = "itinerario2"
                if (uid[0].isUpperCase()) {
                    var slug = "itinerario3"
                }
            }

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

                        DavidGlobals.itinerario = Itinerario(
                            Logradouro(
                                logradouro1Nome,
                                LatLng(
                                    logradouro1Ponto1Lat.toDouble(),
                                    logradouro1Ponto1Lng.toDouble()
                                ),
                                LatLng(
                                    logradouro1Ponto2Lat.toDouble(),
                                    logradouro1Ponto2Lng.toDouble()
                                ),
                                LatLng(
                                    logradouro1Ponto3Lat.toDouble(),
                                    logradouro1Ponto3Lng.toDouble()
                                )
                            ),
                            Logradouro(
                                logradouro2Nome,
                                LatLng(
                                    logradouro2Ponto1Lat.toDouble(),
                                    logradouro2Ponto1Lng.toDouble()
                                ),
                                LatLng(
                                    logradouro2Ponto2Lat.toDouble(),
                                    logradouro2Ponto2Lng.toDouble()
                                ),
                                LatLng(
                                    logradouro2Ponto3Lat.toDouble(),
                                    logradouro2Ponto3Lng.toDouble()
                                )
                            ),
                            Logradouro(
                                logradouro3Nome,
                                LatLng(
                                    logradouro3Ponto1Lat.toDouble(),
                                    logradouro3Ponto1Lng.toDouble()
                                ),
                                LatLng(
                                    logradouro3Ponto2Lat.toDouble(),
                                    logradouro3Ponto2Lng.toDouble()
                                ),
                                LatLng(
                                    logradouro3Ponto3Lat.toDouble(),
                                    logradouro3Ponto3Lng.toDouble()
                                )
                            )
                        )


                        Handler().postDelayed({
                            val intent = Intent(this@MainActivity, MapsActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 1000)

                        return@OnCompleteListener
                    })
        }
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

private fun createFiscalFirestore(){
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser?.uid
    val codigo = createFourDigitNumber()

    val fiscal = hashMapOf(
        "codigo" to codigo,
        "user" to user)


    if (db.collection("fiscais").whereEqualTo("user", user).toString().isEmpty()) {
        db.collection("fiscais")
            .document()
            .set(fiscal)
            .addOnSuccessListener {
                Log.i(TAG, "Fiscal criado com Sucesso ")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao criar fiscal", e)
            }
    }
}

fun createFourDigitNumber(): Int {
    var fourDigitNumber  = ""
    val rangeList = {(0..9).random()}

    while(fourDigitNumber.length < 4)
    {
        val num = rangeList().toString()
        if (!fourDigitNumber.contains(num)) fourDigitNumber +=num
    }

    return fourDigitNumber.toInt()
}