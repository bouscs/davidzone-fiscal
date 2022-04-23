package com.example.davidzonefiscal.entities

import com.google.android.gms.maps.model.LatLng

data class Itinerario(    val rua: String,//rua dos pontos
                          val ponto: LatLng,
                          val ponto2: LatLng,
                          val ponto3: LatLng) {}

