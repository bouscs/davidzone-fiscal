package com.example.davidzonefiscal.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityMapsBinding
import com.example.davidzonefiscal.entities.EnviarLocalizacaoResponse
import com.example.davidzonefiscal.entities.Itinerario
import com.example.davidzonefiscal.entities.Logradouros
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import kotlin.system.exitProcess


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private val logEntry = "MAPS_ITINERARIO";

    /// TODO: Integrar clouud functions
    lateinit var itinerario : Itinerario

    lateinit var logradouros : Logradouros
    var logradouroAtualNo = 0
    lateinit var ptoAtual :  LatLng
    var ptoAtualNo = 0
    var localizacaoAtual: LatLng? = null
    var itinerarioPressed = 0

    //variaveis do mapa
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var marker: Marker
    private lateinit var key: String

    private val linhas: MutableList<Polyline> = mutableListOf()

    //constante usada na verificação da permissao de localizacao e atualização de localização
    private val LOCATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var logradouro1Nome : String = intent.getStringExtra("logradouro1Nome")!!
        var logradouro1Ponto1Lng : String = intent.getStringExtra("logradouro1Ponto1Lng")!!
        var logradouro1Ponto1Lat : String = intent.getStringExtra("logradouro1Ponto1Lat")!!
        var logradouro1Ponto2Lng : String = intent.getStringExtra("logradouro1Ponto2Lng")!!
        var logradouro1Ponto2Lat : String = intent.getStringExtra("logradouro1Ponto2Lat")!!
        var logradouro1Ponto3Lng : String = intent.getStringExtra("logradouro1Ponto3Lng")!!
        var logradouro1Ponto3Lat : String = intent.getStringExtra("logradouro1Ponto3Lat")!!

        var logradouro2Nome : String = intent.getStringExtra("logradouro2Nome")!!
        var logradouro2Ponto1Lng : String = intent.getStringExtra("logradouro2Ponto1Lng")!!
        var logradouro2Ponto1Lat : String = intent.getStringExtra("logradouro2Ponto1Lat")!!
        var logradouro2Ponto2Lng : String = intent.getStringExtra("logradouro2Ponto2Lng")!!
        var logradouro2Ponto2Lat : String = intent.getStringExtra("logradouro2Ponto2Lat")!!
        var logradouro2Ponto3Lng : String = intent.getStringExtra("logradouro2Ponto3Lng")!!
        var logradouro2Ponto3Lat : String = intent.getStringExtra("logradouro2Ponto3Lat")!!

        var logradouro3Nome : String = intent.getStringExtra("logradouro3Nome")!!
        var logradouro3Ponto1Lng : String = intent.getStringExtra("logradouro3Ponto1Lng")!!
        var logradouro3Ponto1Lat : String = intent.getStringExtra("logradouro3Ponto1Lat")!!
        var logradouro3Ponto2Lng : String = intent.getStringExtra("logradouro3Ponto2Lng")!!
        var logradouro3Ponto2Lat : String = intent.getStringExtra("logradouro3Ponto2Lat")!!
        var logradouro3Ponto3Lng : String = intent.getStringExtra("logradouro3Ponto3Lng")!!
        var logradouro3Ponto3Lat : String = intent.getStringExtra("logradouro3Ponto3Lat")!!


        var itinerario1 = Itinerario(
            Logradouros(
                logradouro1Nome,
                LatLng(logradouro1Ponto1Lng.toDouble() , logradouro1Ponto1Lat.toDouble() ),
                LatLng(logradouro1Ponto2Lng.toDouble(),  logradouro1Ponto2Lat.toDouble() ),
                LatLng(logradouro1Ponto3Lng.toDouble(),  logradouro1Ponto3Lat.toDouble())
            ),

            Logradouros(
                logradouro2Nome,
                LatLng(logradouro2Ponto1Lng.toDouble() , logradouro2Ponto1Lat.toDouble() ),
                LatLng(logradouro2Ponto2Lng.toDouble(),  logradouro2Ponto2Lat.toDouble() ),
                LatLng(logradouro2Ponto3Lng.toDouble(),  logradouro2Ponto3Lat.toDouble())
            ),

            Logradouros(
                logradouro3Nome,
                LatLng(logradouro3Ponto1Lng.toDouble() , logradouro3Ponto1Lat.toDouble() ),
                LatLng(logradouro3Ponto2Lng.toDouble(),  logradouro3Ponto2Lat.toDouble() ),
                LatLng(logradouro3Ponto3Lng.toDouble(),  logradouro3Ponto3Lat.toDouble())
            )
        )

        Log.i(logEntry, itinerario1.logradouro1.ponto.toString() ) // Ponto Atual
        Log.i(logEntry, itinerario1.logradouro1.toString() )
        Log.i(logEntry, itinerario1.logradouro2.toString() )
        Log.i(logEntry, itinerario1.logradouro3.toString() )


        itinerario = Itinerario(
            Logradouros(
                "R. Dna Amélia de Paula - Jardim Leonor",
                LatLng(-22.922795035713108, -47.0600505551868),
                LatLng(-22.92310628453413, -47.058097894565414),
                LatLng(-22.922735745230273, -47.06178326071288)
            ),

            Logradouros(
                "Avenida Reitor Benedito José Barreto Fonseca - Parque dos Jacarandás",
                LatLng(-22.83434768818629, -47.05089004881388),
                LatLng(-22.834604787803247, -47.05212923151062),
                LatLng(-22.834352644618864, -47.052606666596645)
            ),

            Logradouros(
                "Rua São Luís do Paraitinga - Jardim do Trevo",
                LatLng(-22.9260127049913, -47.07022138755717),
                LatLng(-22.92398115477799, -47.06914812011016),
                LatLng(-22.928908744150416, -47.07202666336667)
            )
        )


        logradouros = itinerario.logradouro1
        ptoAtual = logradouros.ponto

        val bottomSheetFragmento = BottomSheetFragmento()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        functions = Firebase.functions("southamerica-east1")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used. e outras coisas do maps
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        key = getString(R.string.directions_api_key)

        //botao iniciar itinerario
        binding.tvIniciarItinerario.setOnClickListener {
            //pegando os pontos do itinerario  (pega do bd mas nesse caso vou so usar qualquer coisa)

            itinerarioPressed = 1

            binding.tvIniciarItinerario.visibility = View.GONE
            binding.tvTempoRestante.visibility = View.VISIBLE
            binding.Timer.visibility = View.VISIBLE
            binding.botoes.visibility = View.VISIBLE
            binding.btnTimer.visibility = View.VISIBLE

            //mMap.clear()
            marker = createMarker(ptoAtual, logradouros.rua)
            //getDirectionLine(getDirection(localizacaoAtual, ptoAtual))


            //Botao do timer
            binding.btnTimer.setOnClickListener {
                timerbotao(binding.btnTimer)
                localizacaoAtual?.let { it1 ->
                    pegaDistancia(
                        ptoAtual,
                        it1
                    )
                }
            }
        }

        //Botao que define proximo pto
        binding.btnNext.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("ATENÇÃO!")
            builder.setMessage("Deseja ir para o proximo ponto do itinerário?")
            builder.setPositiveButton("Sim") { dialog, which ->
                //*********************************************************************** FAZER O NEGOCIO
                when (ptoAtualNo) {
                    0 -> {
                        ptoAtualNo = 1
                        ptoAtual = logradouros.ponto2
                    }
                    1 -> {
                        ptoAtualNo = -1
                        ptoAtual = logradouros.ponto3
                    }
                    -1 -> {
                        ptoAtualNo = 0
                        when (logradouroAtualNo) {
                            0 -> {
                                logradouroAtualNo = 1
                                logradouros = itinerario.logradouro2
                            }
                            1 -> {
                                logradouroAtualNo = -1
                                logradouros = itinerario.logradouro3
                            }
                            -1 -> {
                                //VOCE TERMINOU SEU ITINERARIO PARABENS BOA MANO
                                Toast.makeText(
                                    this,
                                    "Você terminou seu expediente!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                                exitProcess(0)
                            }
                        }
                        ptoAtual = logradouros.ponto
                    }
                }
                binding.btnNext.visibility = View.GONE
                binding.btnTimer.visibility = View.VISIBLE
                binding.btnConsultar.visibility = View.GONE
                binding.btnregistradireto.visibility = View.GONE
                binding.Timer.visibility = View.VISIBLE
                binding.tvTempoRestante.text = getString(R.string.tmp_rest)

                //mMap.clear()
                marker.remove()
                marker = createMarker(ptoAtual, logradouros.rua)
                //getDirectionLine(getDirection(localizacaoAtual, ptoAtual))
            }
            builder.setNeutralButton("Cancelar") { _, _ ->
                Toast.makeText(this, "Você cancelou a ação.", Toast.LENGTH_SHORT).show()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.RED)
        }


        // Botão regsitrar irregularidade diretamente.
        binding.btnregistradireto.setOnClickListener {
            val intentRegistroIrregularidadeActivity =
                Intent(this@MapsActivity, SelecionarIrregularidadeActivity::class.java)
            startActivity(intentRegistroIrregularidadeActivity)
        }

        // Botão consultar placa.
        binding.btnConsultar.setOnClickListener {
            val intentConsult = Intent(this@MapsActivity, ConsultarActivity::class.java)
            startActivity(intentConsult)
        }

        // Botão consultar itinerário.
        binding.btnItinerario.setOnClickListener {
            timerbotao(binding.btnConsultar)
            if (!bottomSheetFragmento.isAdded)
                bottomSheetFragmento.show(supportFragmentManager, "BottomSheetDialog")
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptoAtual, 15f))
        }
    }

    private fun updateUITimer(distance: Double) {
        if (distance <= 10000000000000) {
            binding.Timer.visibility = View.GONE
            binding.btnTimer.visibility = View.GONE
            binding.btnNext.visibility = View.VISIBLE
            binding.btnregistradireto.visibility = View.VISIBLE
            binding.btnConsultar.visibility = View.VISIBLE
            binding.tvTempoRestante.text = getString(R.string.prox_pto)

            //mMap.clear()
            marker.remove()
            marker = createMarker(ptoAtual, logradouros.rua)
            //getDirectionLine(getDirection(localizacaoAtual, ptoAtual))

        } else Toast.makeText(this, "O usuário não está no ponto designado!", Toast.LENGTH_LONG)
            .show()
    }

    //função de timer pra botões pressionados
    private fun timerbotao(botao: FloatingActionButton) {
        val timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                botao.setEnabled(false)
            }

            override fun onFinish() {
                botao.setEnabled(true)
            }
        }
        timer.start()
    }


    //funcoes para calcular distancias em 2 ptos da esfera terrestre (haversine formula)
    //***************************************///TEM QUE ESTAR NO FUNCTIONS///************************************************
    /// TODO: Colocar em clouud functions
    private fun callEnviarLocalizacao(pto1: LatLng, pto2: LatLng): Task<String> {
        val data = hashMapOf(
            "localizacao" to hashMapOf(
                "lat" to pto1.latitude,
                "long" to pto1.longitude
            ),
            "pontoItinerario" to hashMapOf(
                "lat" to pto2.latitude,
                "long" to pto2.longitude
            ),
            "uid" to "a"
        )
        return functions
            .getHttpsCallable("enviarLocalizacao")
            .call(data)
            .continueWith { task ->
                val result = gson.toJson(task.result?.data)
                result
            }
    }

    private fun pegaDistancia(pto1: LatLng, pto2: LatLng) {

        callEnviarLocalizacao(pto1, pto2)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        val code = e.code
                        val details = e.details
                    }
                    Snackbar.make(
                        binding.tvTempoRestante,
                        "Erro no servidor. Se o problema persistir ligue 0800-000-0000",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Tente novamente") {
                            pegaDistancia(pto1, pto2)
                        }
                        .show()
                    return@OnCompleteListener
                }

                val result = task.result

                val genericConsultRes = gson.fromJson(result, GenericFunctionResponse::class.java)

                val message = genericConsultRes.resultConsult.message
                val status = genericConsultRes.resultConsult.status

                if (status == "SUCCESS") {
                    val successResponse =
                        gson.fromJson(result, EnviarLocalizacaoResponse::class.java)
                    val distancia = successResponse.result.payload.distanciaKm * 1000
                    val desvio = successResponse.result.payload.desvio
                    Log.d("aaaaaaaa", distancia.toString())

                    updateUITimer(distancia)

                }
            })
    }


    //Coisas do mapa
    //*********************************************************************************************
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        val ptoDefault = LatLng(-22.910002734059237, -47.06436548707138)
        //localizacaoAtual = ptoDefault
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptoDefault, 9f))
        getLocAccess()
        //val puc = LatLng(-22.834065, -47.052522)
        //mMap.addMarker(MarkerOptions().position(ptoAtual).title("Marker in PUC"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptoAtual, 15f))

    }

    //consegue acesso a localizacao
    private fun getLocAccess() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            getLocationUpdates()
            startLocationUpdates()
        } else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION
            )
    }

    //consegue updates de localização no tempo definido
    private fun getLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    localizacaoAtual = LatLng(location.latitude, location.longitude)
                    if (itinerarioPressed == 1) getDirectionLine(getDirection(localizacaoAtual!!,ptoAtual))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    //elementos no mapa
    //marker
    private fun createMarker(pto: LatLng, string: String): Marker {
        val marker: Marker = mMap.addMarker(MarkerOptions().position(pto).title(string))!!
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pto, 15f))
        return marker
    }


    //funcionalidade de gps
    private fun getDirection(origem: LatLng, destino: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origem.latitude},${origem.longitude}&destination=${destino.latitude},${destino.longitude}&key=${key}"
    }

    private fun getDirectionLine(string: String) {
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections = string
        val directionsRequest = object :
            StringRequest(Method.GET, urlDirections, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                // Get routes
                val routes = jsonResponse.getJSONArray("routes")
                val legs = routes.getJSONObject(0).getJSONArray("legs")
                val steps = legs.getJSONObject(0).getJSONArray("steps")
                for (i in 0 until steps.length()) {
                    val points =
                        steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                    path.add(PolyUtil.decode(points))
                }

                for (i in 0 until linhas.size) {
                    linhas[i].remove()
                }

                linhas.clear()

                for (i in 0 until path.size) {
                    linhas.add(this.mMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED)))
                }
            }, Response.ErrorListener { _ ->
            }) {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }
}