package com.example.davidzonefiscal.entities

data class EnviarLocalizacaoResponse (
    val result: EnviarLocalizacaoResponseResponse
)

data class EnviarLocalizacaoResponseResponse(val message : String,
                                             val status : String,
                                             val payload: EnviarLocalizacaoPayload)

data class EnviarLocalizacaoPayload(val distanciaKm: Double, val desvio: Boolean, val uid: String)