package com.example.copernisea

import android.app.Application
import android.content.Context
import com.azure.android.maps.control.AzureMaps

class CoperniSeaApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AzureMaps.setSubscriptionKey("YOURKEYHERE")
    }
}
