package com.example.davidzonefiscal.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityMapsBinding
import com.example.davidzonefiscal.entities.Itinerario
import com.example.davidzonefiscal.entities.Pontos
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var binding: ActivityMapsBinding
    private lateinit var marker: Marker

    //todos os 9 pontos de um determinado itinerario
    private lateinit var ptoAtual: LatLng
    private lateinit var ptoAtualNo: Number
    private lateinit var localizacaoAtual: LatLng

    private val LOCATION_PERMISSION = 1

    //instancia de itinerario
    private lateinit var itinerario: Itinerario


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bottomSheetFragmento = BottomSheetFragmento()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used. e outras coisas do maps
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        //botao iniciar itinerario
        binding.tvIniciarItinerario.setOnClickListener {
            //pegando os pontos do itinerario  (pega do bd mas nesse caso vou so usar qualquer coisa)
            itinerario = Itinerario(
                "Rua São Luís do Paraitinga - Jardim do Trevo",
                LatLng(-22.9260127049913, -47.07022138755717),
                LatLng(-22.928908744150416, -47.07202666336667),
                LatLng(-22.92398115477799, -47.06914812011016)
            )
            binding.tvIniciarItinerario.visibility = View.GONE
            binding.tvTempoRestante.visibility = View.VISIBLE
            binding.Timer.visibility = View.VISIBLE
            binding.botoes.visibility = View.VISIBLE
            binding.btnTimer.visibility = View.VISIBLE
            ptoAtual = itinerario.ponto
            ptoAtualNo = 0
            marker = createMarker(ptoAtual)
        }

        //Botao do timer
        binding.btnTimer.setOnClickListener{
            if (getDistance(ptoAtual, localizacaoAtual)<=100){
                binding.Timer.visibility = View.GONE
                binding.btnTimer.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                binding.btnregistradireto.visibility = View.VISIBLE
                binding.btnConsultar.visibility = View.VISIBLE
                binding.tvTempoRestante.text =  getString(R.string.prox_pto)
            } else Toast.makeText(this, "Usuario não está no ponto designado!", Toast.LENGTH_LONG).show()
        }

        //Botao que define proximo pto
        binding.btnNext.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("ATENÇÃO!")
            builder.setMessage("Deseja ir para o proximo ponto do logradouro?")
            builder.setPositiveButton("Sim"){dialog, which ->
                Toast.makeText(this,"Mudando para o próximo ponto.",Toast.LENGTH_SHORT).show()
                //*********************************************************************** FAZER O NEGOCIO
                when (ptoAtualNo) {
                    0 -> {
                        ptoAtualNo = 1
                        ptoAtual = itinerario.ponto2
                    }
                    1 -> {
                        ptoAtualNo = -1
                        ptoAtual = itinerario.ponto3
                    }
                    -1 -> {
                        ptoAtualNo = 0
                        //********pega outro do bd**************
                        itinerario = Itinerario(
                            "Avenida Reitor Benedito José Barreto Fonseca - Parque dos Jacarandás",
                            LatLng(-22.83434768818629, -47.05089004881388),
                            LatLng(-22.834352644618864, -47.052606666596645),
                            LatLng(-22.834604787803247, -47.05212923151062)
                        )
                       ptoAtual = itinerario.ponto
                    }
                }
                binding.btnNext.visibility = View.GONE
                binding.btnTimer.visibility = View.VISIBLE
                binding.Timer.visibility = View.VISIBLE
                binding.tvTempoRestante.text = getString(R.string.tmp_rest)
                marker.remove()
                marker = createMarker(ptoAtual)
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
            bottomSheetFragmento.show(supportFragmentManager, "BottomSheetDialog")
            //val puc = LatLng(-22.834065, -47.052522)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptoAtual, 15f))
        }
    }

    //funcoes para calcular distancias em 2 ptos da esfera terrestre
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


    //Coisas do mapa
    //*******************************
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

    //usa o mylocation se permitida a utilizacao do gps
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                mMap.isMyLocationEnabled = true
            }
            else {
                Toast.makeText(this, "Usuario não permitiu o uso do GPS", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun getLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 100
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        localizacaoAtual = LatLng(location.latitude, location.longitude)
                    }
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-22.910002734059237, -47.06436548707138),9f))
        getLocAccess()
        //val puc = LatLng(-22.834065, -47.052522)
        //mMap.addMarker(MarkerOptions().position(ptoAtual).title("Marker in PUC"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptoAtual, 15f))

    }

    private fun createMarker(pto: LatLng): Marker {
        val marker: Marker = mMap.addMarker(MarkerOptions().position(pto).title("Ponto atual"))!!
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pto,15f))
        return marker
    }





}