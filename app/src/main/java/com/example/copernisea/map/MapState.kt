package com.example.copernisea.map

sealed class MapState {

    object Loading: MapState()

    object FinishedLoading: MapState()

    data class ShowLayer(
        val layerUrl: String,
        val scaleUrl: String,
        val date: String
    ): MapState()

    data class Error(
        val message: String
    ): MapState()
}