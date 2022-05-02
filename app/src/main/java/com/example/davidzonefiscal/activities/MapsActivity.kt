package com.example.davidzonefiscal.activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.example.davidzonefiscal.entities.DavidGlobals
import com.example.davidzonefiscal.entities.EnviarLocalizacaoResponse
import com.example.davidzonefiscal.entities.Itinerario
import com.example.davidzonefiscal.entities.Logradouro
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

    private lateinit var timer: CountDownTimer
    private var timerIntervalo = 1000L
    private val tempo1 = 300000L
    private val tempo2 = 1200000L
    private lateinit var runnable: Runnable
    private var handler = Handler(Looper.getMainLooper())

    private lateinit var binding: ActivityMapsBinding
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    // private val logEntry = "MAPS_ITINERARIO";

    lateinit var logradouros : Logradouro
    var logradouroAtualNo = 0
    lateinit var ptoAtual :  LatLng
    var ptoAtualNo = 0
    var localizacaoAtual: LatLng? = null
    var localizacaoAnterior: LatLng? = null
    var itinerarioPressed = 0

    lateinit var itinerario: Itinerario

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

        itinerario = DavidGlobals.itinerario

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
            binding.TimerMin.visibility = View.VISIBLE
            binding.TimerSeg.visibility = View.VISIBLE
            binding.botoes.visibility = View.VISIBLE

            timerVisual(tempo1)
            timer.start()
            timerBackground(tempo1)


            //mMap.clear()
            marker = createMarker(ptoAtual, logradouros.rua)
            //getDirectionLine(getDirection(localizacaoAtual, ptoAtual))

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
                binding.btnConsultar.visibility = View.GONE
                binding.btnregistradireto.visibility = View.GONE
                binding.TimerMin.visibility = View.VISIBLE
                binding.TimerSeg.visibility = View.VISIBLE
                binding.tvTempoRestante.text = getString(R.string.tmp_rest)

                timer.cancel()
                timerVisual(tempo1)
                timer.start()

                marker.remove()
                marker = createMarker(ptoAtual, logradouros.rua)
            }
            builder.setNeutralButton("Cancelar") { _, _ ->
                Toast.makeText(this, "Você cancelou a ação.", Toast.LENGTH_SHORT).show()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4886FF"))
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
            if (!bottomSheetFragmento.isAdded)
                bottomSheetFragmento.show(supportFragmentManager, "BottomSheetDialog")
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptoAtual, 15f))
        }
    }



    //funcoes para calcular distancias em 2 ptos
    //***************************************************************************************
    //funcoes para calcular distancias em 2 ptos da esfera terrestre (Formula de Haversine)
    private fun rad(x: Double): Double {return x * kotlin.math.PI / 180}
    private fun getDistance(p1: LatLng, p2: LatLng): Double {

        val r = 6378137 // Raio da Terra
        val dLat = rad(p2.latitude - p1.latitude)
        val dLong = rad(p2.longitude - p1.longitude)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(rad(p1.latitude)) * kotlin.math.cos(rad(p2.latitude)) *
                kotlin.math.sin(dLong / 2) * kotlin.math.sin(dLong / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c // retorna a distancia em metros

    }
    //funcoes para calcular distancias em 2 ptos firebase func
    //***************************************************************************************
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

                }
            })
    }




    //funcoes relacionadas ao tempo e timers
    //***************************************************************************************

    private fun timerVisual(tempoinicial: Long) {
        val tempoInicialRestanteMin = (tempoinicial / 1000) / 60
        binding.TimerMin.text = tempoInicialRestanteMin.toString()
        binding.TimerSeg.text = "00"
        timer = object: CountDownTimer(tempoinicial, timerIntervalo){
            override fun onTick(milisAteOFim: Long) {
                val tempoRestanteSegundos = (milisAteOFim / 1000) % 60
                val tempoRestanteMinutos = (milisAteOFim / 1000) / 60
                binding.TimerMin.text = tempoRestanteMinutos.toString() + ":"
                if (tempoRestanteSegundos<10) binding.TimerSeg.text = "0" + tempoRestanteSegundos.toString()
                else binding.TimerSeg.text = tempoRestanteSegundos.toString()
            }

            override fun onFinish() {
                localizacaoAtual?.let { getDistance(it, ptoAtual) }?.let { updateUITimer(it) }
            }
        }
    }

    private fun timerBackground(tempoinicial: Long) {
        runnable = Runnable {

            if (localizacaoAnterior != null)
            {
                if ( (getDistance(localizacaoAnterior!!,ptoAtual)) < (getDistance(localizacaoAtual!!,ptoAtual)))
                    {
                        localizacaoAtual?.let { pegaDistancia(it, ptoAtual) }
                        Toast.makeText(this, "Desvio registrado", Toast.LENGTH_LONG)
                        .show()
                    } else if ( getDistance(localizacaoAnterior!!,localizacaoAtual!!) < 100
                                && getDistance(localizacaoAtual!!, ptoAtual) > 100 ) aviso()
            }
            localizacaoAnterior = localizacaoAtual
           // Toast.makeText(this, "Deu certo!", Toast.LENGTH_LONG)
               // .show()

            handler.postDelayed(runnable, tempoinicial)

        }

        handler.postDelayed(runnable, tempoinicial)
    }

    private fun updateUITimer(distance: Double) {
        if (distance <= 100) {
            binding.TimerMin.visibility = View.GONE
            binding.TimerSeg.visibility = View.GONE
            binding.btnNext.visibility = View.VISIBLE
            binding.btnregistradireto.visibility = View.VISIBLE
            binding.btnConsultar.visibility = View.VISIBLE
            binding.tvTempoRestante.text = getString(R.string.prox_pto)

            marker.remove()
            marker = createMarker(ptoAtual, logradouros.rua)

        } else aviso()

    }

    private fun aviso(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ATENÇÃO!")
        builder.setMessage("Dirija-se para o ponto marcado no mapa!!!")
        builder.setPositiveButton("OK") { dialog, which ->
            timer.cancel()
            timerVisual(tempo2)
            timer.start()
        }
        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4886FF"))
    }

    private fun checaSempre(distance: Double) {
        if (distance <= 100) {
            binding.TimerMin.visibility = View.GONE
            binding.TimerSeg.visibility = View.GONE
            binding.btnNext.visibility = View.VISIBLE
            binding.btnregistradireto.visibility = View.VISIBLE
            binding.btnConsultar.visibility = View.VISIBLE
            binding.tvTempoRestante.text = getString(R.string.prox_pto)
            
        } else {
                binding.TimerMin.visibility = View.VISIBLE
                binding.TimerSeg.visibility = View.VISIBLE
                binding.btnNext.visibility = View.GONE
                binding.btnregistradireto.visibility = View.GONE
                binding.btnConsultar.visibility = View.GONE
                binding.tvTempoRestante.text = getString(R.string.tmp_rest)
        }
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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.mapstyle2
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }



        mMap.uiSettings.isZoomControlsEnabled = true
        val ptoDefault = LatLng(-22.910002734059237, -47.06436548707138)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptoDefault, 9f))
        getLocAccess()
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
                    if (itinerarioPressed == 1) {
                        checaSempre(getDistance(localizacaoAtual!!,ptoAtual))
                        getDirectionLine(getDirection(localizacaoAtual!!,ptoAtual))
                    }
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
        val directionsRequest = object :
            StringRequest(Method.GET, string, Response.Listener { response ->
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
                    linhas.add(
                        this.mMap.addPolyline(
                            PolylineOptions().addAll(path[i]).color(Color.RED)
                        )
                    )
                }
            }, Response.ErrorListener {
            }) {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }
}