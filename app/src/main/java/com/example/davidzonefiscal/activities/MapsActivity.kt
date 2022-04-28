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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityMapsBinding
import com.example.davidzonefiscal.entities.Itinerario
import com.example.davidzonefiscal.entities.Logradouros
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.maps.android.PolyUtil
import org.json.JSONObject

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private val logEntry = "MAPS_ITINERARIO";


    // variaveis de itinerario e localização
    // FUNCTION PRA PEGAR DO BD
    // TODO: Integrar clound functions
    lateinit var itinerario : Itinerario
    lateinit var logradouros : Logradouros
    lateinit var logradouroAtualNo : Number
    lateinit var ptoAtual : LatLng
    lateinit var ptoAtualNo : Number

    var localizacaoAtual: LatLng? = null

    //variaveis do mapa
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var binding: ActivityMapsBinding
    private lateinit var marker: Marker
    private lateinit var linha: Polyline
    private lateinit var key: String

    //constante usada na verificação da permissao de localizacao e atualização de localização
    private val LOCATION_PERMISSION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        functions = Firebase.functions("southamerica-east1")

        itinerario = Itinerario(
            Logradouros("R. Dna Amélia de Paula - Jardim Leonor",
                LatLng(-22.922795035713108, -47.0600505551868),
                LatLng(-22.92310628453413, -47.058097894565414),
                LatLng(-22.922735745230273, -47.06178326071288)),

            Logradouros("Avenida Reitor Benedito José Barreto Fonseca - Parque dos Jacarandás",
                LatLng(-22.83434768818629, -47.05089004881388),
                LatLng(-22.834604787803247, -47.05212923151062),
                LatLng(-22.834352644618864, -47.052606666596645)),

            Logradouros("Rua São Luís do Paraitinga - Jardim do Trevo",
                LatLng(-22.9260127049913, -47.07022138755717),
                LatLng(-22.92398115477799, -47.06914812011016),
                LatLng(-22.928908744150416, -47.07202666336667))
        )

        // onGetItinerarioLoad()
        // aparentemente latinit vars não pode ser inicializado dentro de uma função, deve ser feito no body da classe

        val bottomSheetFragmento = BottomSheetFragmento()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logradouros = itinerario.logradouro1
        logradouroAtualNo = 0
        ptoAtual = logradouros.ponto
        ptoAtualNo = 0

        // Obtain the SupportMapFragment and get notified when the map is ready to be used. e outras coisas do maps
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        key = getString(R.string.directions_api_key)

        //botao iniciar itinerario
        binding.tvIniciarItinerario.setOnClickListener {
            //pegando os pontos do itinerario  (pega do bd mas nesse caso vou so usar qualquer coisa)

            binding.tvIniciarItinerario.visibility = View.GONE
            binding.tvTempoRestante.visibility = View.VISIBLE
            binding.Timer.visibility = View.VISIBLE
            binding.botoes.visibility = View.VISIBLE
            binding.btnTimer.visibility = View.VISIBLE

            //mMap.clear()
            marker = createMarker(ptoAtual, logradouros.rua)
            //getDirectionLine(getDirection(localizacaoAtual, ptoAtual))

            //Botao do timer
            binding.btnTimer.setOnClickListener{
                timerbotao(binding.btnTimer)
                if (checadistancia()) {
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
        }

        //Botao que define proximo pto
        binding.btnNext.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("ATENÇÃO!")
            builder.setMessage("Deseja ir para o proximo ponto do itinerário?")
            builder.setPositiveButton("Sim"){dialog, which ->
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
                                Toast.makeText(this,"Você terminou seu expediente!!!",Toast.LENGTH_SHORT).show()
                                finish();
                                System.exit(0);
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
            builder.setNeutralButton("Cancelar"){_,_ ->
                Toast.makeText(this,"Você cancelou a ação.",Toast.LENGTH_SHORT).show()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.RED)
        }


        // Botão regsitrar irregularidade diretamente.
        binding.btnregistradireto.setOnClickListener{
            val intentRegistroIrregularidadeActivity = Intent(this@MapsActivity, SelecionarIrregularidadeActivity::class.java)
            startActivity(intentRegistroIrregularidadeActivity)
        }

        // Botão consultar placa.
        binding.btnConsultar.setOnClickListener{
            val intentConsult = Intent(this@MapsActivity, ConsultarActivity::class.java)
            startActivity(intentConsult)
        }

        // Botão consultar itinerário.
        binding.btnItinerario.setOnClickListener {
            timerbotao(binding.btnConsultar)
            if(!bottomSheetFragmento.isAdded)
                bottomSheetFragmento.show(supportFragmentManager, "BottomSheetDialog")
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptoAtual, 15f))
        }
    }

    private fun getItinerario (slug: String): Task<String>  {
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

    private fun onGetItinerarioLoad() {
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

                itinerario = Itinerario(
                    Logradouros(getItinerarioRes.result.payload.itinerario.logradouros[0].nome,
                        LatLng(getItinerarioRes.result.payload.itinerario.logradouros[0].pontos[0]._latitude, getItinerarioRes.result.payload.itinerario.logradouros[0].pontos[0]._longitude),
                        LatLng(getItinerarioRes.result.payload.itinerario.logradouros[0].pontos[1]._latitude, getItinerarioRes.result.payload.itinerario.logradouros[1].pontos[0]._longitude),
                        LatLng(getItinerarioRes.result.payload.itinerario.logradouros[0].pontos[2]._latitude, getItinerarioRes.result.payload.itinerario.logradouros[2].pontos[0]._longitude)),

                    Logradouros(getItinerarioRes.result.payload.itinerario.logradouros[1].nome,
                        LatLng(getItinerarioRes.result.payload.itinerario.logradouros[1].pontos[0]._latitude, getItinerarioRes.result.payload.itinerario.logradouros[0].pontos[0]._longitude),
                        LatLng(getItinerarioRes.result.payload.itinerario.logradouros[1].pontos[1]._latitude, getItinerarioRes.result.payload.itinerario.logradouros[1].pontos[0]._longitude),
                        LatLng(getItinerarioRes.result.payload.itinerario.logradouros[1].pontos[2]._latitude, getItinerarioRes.result.payload.itinerario.logradouros[2].pontos[0]._longitude)),

                    Logradouros(getItinerarioRes.result.payload.itinerario.logradouros[2].nome,
                        LatLng(getItinerarioRes.result.payload.itinerario.logradouros[2].pontos[0]._latitude, getItinerarioRes.result.payload.itinerario.logradouros[0].pontos[0]._longitude),
                        LatLng(getItinerarioRes.result.payload.itinerario.logradouros[2].pontos[1]._latitude, getItinerarioRes.result.payload.itinerario.logradouros[1].pontos[0]._longitude),
                        LatLng(getItinerarioRes.result.payload.itinerario.logradouros[2].pontos[2]._latitude, getItinerarioRes.result.payload.itinerario.logradouros[2].pontos[0]._longitude)),
                )

                return@OnCompleteListener
            })
    }

    //função de timer pra botões pressionados
    fun timerbotao(botao: FloatingActionButton)
    {
        val timer = object: CountDownTimer(5000, 1000) {
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
    private fun rad(x: Double): Double {return x * kotlin.math.PI / 180}
    private fun getDistance(p1: LatLng, p2: LatLng): Double {

        var r = 6378137; // Raio da Terra
        var dLat = rad(p2.latitude - p1.latitude)
        var dLong = rad(p2.longitude - p1.longitude)
        var a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(rad(p1.latitude)) * kotlin.math.cos(rad(p2.latitude)) *
                kotlin.math.sin(dLong / 2) * kotlin.math.sin(dLong / 2)
        var c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        var d = r * c
        return d // retorna a distancia em metros
    }

    private fun checadistancia(): Boolean{
        if (localizacaoAtual!=null) return getDistance(ptoAtual, localizacaoAtual!!)<=100
        else return false
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
        val ptoDefault = LatLng(-22.910002734059237, -47.06436548707138)
        //localizacaoAtual = ptoDefault
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptoDefault,9f))
        getLocAccess()
        //val puc = LatLng(-22.834065, -47.052522)
        //mMap.addMarker(MarkerOptions().position(ptoAtual).title("Marker in PUC"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptoAtual, 15f))

    }

    //consegue acesso a localizacao
    private fun getLocAccess() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            getLocationUpdates()
            startLocationUpdates()
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
    }

    private fun getLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 1000
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    localizacaoAtual = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }


    //elementos no mapa
    private fun createMarker(pto: LatLng, string: String): Marker {
        val marker: Marker = mMap.addMarker(MarkerOptions().position(pto).title(string))!!
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pto,15f))
        return marker
    }


    /// TODO: Implementar funcionalidade de gps
    private fun getDirection(origem: LatLng, destino: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origem.latitude},${origem.longitude}&destination=${destino.latitude},${destino.longitude}&key=${key}"
    }

    private fun getDirectionLine(string: String) {
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections = string
        val directionsRequest = object : StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> {
                response ->
            val jsonResponse = JSONObject(response)
            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                linha = this.mMap!!.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
            }
        }, Response.ErrorListener {
                _ ->
        }){}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }

}