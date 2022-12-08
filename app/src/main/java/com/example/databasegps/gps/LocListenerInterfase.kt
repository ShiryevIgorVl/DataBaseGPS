package com.example.databasegps.gps

import android.location.Location


interface LocListenerInterfase {
    fun onGetLocation(location: Location)
//  fun subscriptionLocation(location: Location)
}