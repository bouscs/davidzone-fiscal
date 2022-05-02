package com.example.davidzonefiscal.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetItinerarioResponse (
    val result : GetItinerarioResponseResponse
) : Parcelable

@Parcelize
data class GetItinerarioResponseResponse(
    val payload : PayloadItinerario,
    val message : String,
    val status : String
) : Parcelable

@Parcelize
data class PayloadItinerario(
    val itinerario : _Itinerario,
) : Parcelable

@Parcelize
data class _Itinerario(
    val logradouros : List<_Logradouros>,
    val slug : String
) : Parcelable

@Parcelize
data class _Logradouros(
    val pontos : List<Coordenations>,
    val nome : String
) : Parcelable

@Parcelize
data class Coordenations(
    val _longitude : Double,
    val _latitude : Double
) : Parcelable