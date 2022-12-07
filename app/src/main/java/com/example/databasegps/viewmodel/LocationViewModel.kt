package com.example.databasegps.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class LocationViewModel() : ViewModel() {
    val locationLiveData: MutableLiveData<Location> by lazy {
        MutableLiveData<Location>()
    }

    fun getLocationLiveData (): LiveData<Location> {
        return locationLiveData
    }
}


