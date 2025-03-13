package com.example.KYL.GPX

data class WPTpoint(
    var lat: Float = 0f,
    var lon: Float = 0f,
    val ele: Float = 0.000000f,
    var time: String = "",
    var name:String = "",
    var cmt: String = "",
    var desc: String =""
    )
