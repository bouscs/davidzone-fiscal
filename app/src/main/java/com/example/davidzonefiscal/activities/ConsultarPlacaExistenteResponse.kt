package com.example.davidzonefiscal.activities

data class ConsultarPlacaExistenteResponse (
    val result : ResultConsult
)

data class ResultConsult(
    val payload : Payload,
    val message : String,
    val status : String
)

data class Payload(
    val regular : Boolean,
    val placa : String
)



