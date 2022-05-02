package com.example.davidzonefiscal.activities

import com.google.gson.annotations.SerializedName

data class GenericConsultarPlacaResponse (
    @SerializedName(value = "result", alternate = arrayOf("error"))
    val resultConsult : ResultData
)

