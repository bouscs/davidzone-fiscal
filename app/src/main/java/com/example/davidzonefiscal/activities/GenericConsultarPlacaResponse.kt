package com.example.davidzonefiscal.activities

import com.google.gson.annotations.SerializedName

data class ResultData(
    val message : String,
    val status : String
)

data class GenericConsultarPlacaResponse (
    @SerializedName(value = "result", alternate = arrayOf("error"))
    val resultConsult : ResultData
)

