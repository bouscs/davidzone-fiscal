package com.example.davidzonefiscal.activities

data class GetItinerarioResponse (
    val result : ResultItinerario
)

data class ResultItinerario(
    val payload : PayloadItinerario,
    val message : String,
    val status : String
)

data class PayloadItinerario(
    val itinerario : Itinerario,
)

data class Itinerario(
    val logradouros : List<Logradouros>,
    val slug : String
)

data class Logradouros(
    val pontos : List<Coordenations>,
    val nome : String
)

data class Coordenations(
    val _longitude : Double,
    val _latitude : Double
)

