package com.example.davidzonefiscal.entities

data class GetItinerarioResponse (
    val response: GetItinerarioResponseResponse
        )

data class GetItinerarioResponseResponse(val message : String,
                                             val status : String,
                                             val payload: GetItinerarioPayload)

data class GetItinerarioPayload(
    val itinerario: _Itinerario
)

data class _Itinerario(
    val nome: String,
    val pontos: Array<_ItinerarioPonto>
)

data class _ItinerarioPonto(
    val _latitude: Double,
    val _longitude: Double
)