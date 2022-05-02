package com.example.davidzonefiscal.activities

import com.google.gson.annotations.SerializedName

data class ResultData(
    val message : String,
    val status : String
)

data class GenericFunctionResponse (
    @SerializedName(value = "result", alternate = arrayOf("error"))
    val resultConsult : ResultData
)
