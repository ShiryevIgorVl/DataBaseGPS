package com.example.databasegps.gps

import android.location.Location
import android.location.LocationListener


class MyLocation : LocationListener {
    private lateinit var locListenerInterfase: LocListenerInterfase

    override fun onLocationChanged(location: Location) {
        locListenerInterfase.onGetLocation(location)
    }

    fun setLocListenerInterface(locListenerInterfase: LocListenerInterfase) {
        this.locListenerInterfase = locListenerInterfase
    }


}