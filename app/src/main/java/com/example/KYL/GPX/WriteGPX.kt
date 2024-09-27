package com.example.KYL.GPX

import com.example.KYL.constans.MainTime

class WriteGPX(APP_NAME: String) {
    val fileName = APP_NAME + " " + MainTime.getTimeForSaveFile() + ".gpx"


}