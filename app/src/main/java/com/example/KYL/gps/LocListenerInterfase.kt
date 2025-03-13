package com.example.KYL.gps

import android.location.Location


interface LocListenerInterfase {
    fun onGetLocation(location: Location)
}