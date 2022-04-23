package com.example.davidzonefiscal.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityMapsBinding
import com.example.davidzonefiscal.entities.Itinerario
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var binding: ActivityMapsBinding
    private lateinit var ptofinal: LatLng

    private val LOCATION_PERMISSION = 1

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

        //pegando o itinerario  (pega do bd mas nesse caso vou so usar qualquer coisa)
        itinerario =  Itinerario("Rua São Luís do Paraitinga - Jardim do Trevo",
                                      LatLng(-22.9260127049913, -47.07022138755717),
                                      LatLng(-22.928908744150416, -47.07202666336667),
                                      LatLng(-22.92398115477799, -47.06914812011016),
                                 "R. Dna Amélia de Paula - Jardim Leonor",
                                      LatLng(-22.922795035713108, -47.0600505551868),
                                      LatLng(-22.922735745230273, -47.06178326071288),
                                      LatLng(-22.92310628453413, -47.058097894565414),
                                 "Avenida Reitor Benedito José Barreto Fonseca - Parque dos Jacarandás",
                                      LatLng(-22.83434768818629, -47.05089004881388),
                                      LatLng(-22.834352644618864, -47.052606666596645),
                                      LatLng(-22.834604787803247, -47.05212923151062))
        ptofinal = itinerario.ponto3_3

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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptofinal, 15f))
        }
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
        getLocAccess()
        //val puc = LatLng(-22.834065, -47.052522)
        mMap.addMarker(MarkerOptions().position(ptofinal).title("Marker in PUC"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ptofinal, 15f))

    }



}