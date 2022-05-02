package com.example.davidzonefiscal.activities

import android.Manifest
import android.content.ContentValues.TAG
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
import com.example.davidzonefiscal.entities.PayloadItinerario
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder

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

                Handler().postDelayed({
                    val intent = Intent(this@MainActivity, MapsActivity::class.java)
                    intent.putExtra("itinerarioGetter", intinerarioResult)
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