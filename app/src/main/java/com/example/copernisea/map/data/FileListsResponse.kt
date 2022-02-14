package com.example.copernisea.map.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Folder(
    val dataType: String,
    val baseUrl: String,
    val availableDates: List<AvailableDate>
)

@JsonClass(generateAdapter = true)
data class AvailableDate(
    val date: String,
    val layerFileName: String,
    val scaleFileName: String
)