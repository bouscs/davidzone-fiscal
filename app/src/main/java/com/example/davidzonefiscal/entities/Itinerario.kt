package com.example.davidzonefiscal.entities

import com.google.android.gms.maps.model.LatLng

data class Itinerario(    val rua1: String,  // rua do ponto
                          val ponto1_1: LatLng,
                          val ponto1_2: LatLng,
                          val ponto1_3: LatLng,
                          val rua2: String , // rua do ponto
                          val ponto2_1: LatLng,
                          val ponto2_2: LatLng,
                          val ponto2_3: LatLng,
                          val rua3: String,  // rua do ponto
                          val ponto3_1: LatLng,
                          val ponto3_2: LatLng,
                          val ponto3_3: LatLng) {}

