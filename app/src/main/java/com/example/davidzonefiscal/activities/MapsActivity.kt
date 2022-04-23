package com.example.davidzonefiscal.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMapsBinding

    private val LOCATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bottomSheetFragmento = BottomSheetFragmento()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
        }
    }

    private fun getLocAccess() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
    }

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
        //mMap.addMarker(MarkerOptions().position(puc).title("Marker in PUC"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(puc, 15f))

    }



}