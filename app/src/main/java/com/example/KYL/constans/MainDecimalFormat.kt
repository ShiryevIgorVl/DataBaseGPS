package com.example.KYL.constans

import android.icu.text.DecimalFormat

object MainDecimalFormat {
    private lateinit var decimalFormat: DecimalFormat
    //Функции для фоматирования вывода данных GPS API
    fun formatTV(a: Double): String{
        decimalFormat = DecimalFormat("##.######")
      return decimalFormat.format(a).toString()
    }

    fun formatTVAlt(a: Double): String{
        decimalFormat = DecimalFormat("-##,#")
        return decimalFormat.format(a).toString()
    }

    fun formatExcelInt(a: Double): Double{
        decimalFormat = DecimalFormat("##")
        return decimalFormat.format(a).toDouble()
    }

    fun formatExcelTwoSings(a: Double): Double{
        decimalFormat = DecimalFormat("##.##")
        return decimalFormat.format(a).toDouble()
    }

    fun formatTVAcc(a: Float): String{
        decimalFormat = DecimalFormat("##.#")
        return decimalFormat.format(a).toString()
    }
}