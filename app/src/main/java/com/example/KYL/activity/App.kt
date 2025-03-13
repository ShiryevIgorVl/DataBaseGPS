package com.example.KYL.activity

import android.app.Application
import com.example.KYL.database.MainDataBase

class App: Application() {
    val database by lazy { MainDataBase.getDataBase(this) }
}