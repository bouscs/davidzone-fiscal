package com.example.davidzonefiscal.entities

import com.google.android.gms.maps.model.LatLng

data class Logradouro (val rua: String,//rua dos pontos
                       val ponto: LatLng, //(2 doubles, 1 representa latitude)
                       val ponto2: LatLng,
                       val ponto3: LatLng)
