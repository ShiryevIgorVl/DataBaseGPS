package com.example.databasegps.activity

import android.app.Application
import com.example.databasegps.database.MainDataBase

class App: Application() {
    val database by lazy { MainDataBase.getDataBase(this) }
}