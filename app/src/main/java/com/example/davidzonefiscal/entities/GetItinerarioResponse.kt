package com.example.davidzonefiscal.entities

import com.google.android.gms.maps.model.LatLng


data class GetItinerarioResponse (
    val result : GetItinerarioResponseResponse
)

data class GetItinerarioResponseResponse(
    val payload : PayloadItinerario,
    val message : String,
    val status : String
)

data class PayloadItinerario(
    val itinerario : _Itinerario,
)

data class _Itinerario(
    val logradouros : List<_Logradouros>,
    val slug : String
)

data class _Logradouros(
    val pontos : List<Coordenations>,
    val nome : String
)

data class Coordenations(
    val _longitude : Double,
    val _latitude : Double
)