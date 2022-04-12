package com.example.davidzonefiscal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivityMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var btnregistradireto: FloatingActionButton
    private lateinit var btnConsultar: FloatingActionButton
    private lateinit var btnItinerario: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bottomSheetFragmento = BottomSheetFragmento()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnregistradireto = findViewById(R.id.btnregistradireto)
        btnregistradireto.setOnClickListener{
            val intentRegistroIrregularidadeActivity = Intent(this@MapsActivity, SelecionarIrregularidadeActivity::class.java)
            startActivity(intentRegistroIrregularidadeActivity)
        }

        btnConsultar = findViewById(R.id.btnConsultar)
        btnConsultar.setOnClickListener{
            val intentConsult = Intent(this@MapsActivity, ConsultarActivity::class.java)
            startActivity(intentConsult)
        }

        btnItinerario = findViewById(R.id.btnItinerario)
        btnItinerario.setOnClickListener {
            bottomSheetFragmento.show(supportFragmentManager, "BottomSheetDialog")
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

        val puc = LatLng(-22.834065, -47.052522)
        mMap.addMarker(MarkerOptions().position(puc).title("Marker in PUC"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(puc, 15f))
    }
}