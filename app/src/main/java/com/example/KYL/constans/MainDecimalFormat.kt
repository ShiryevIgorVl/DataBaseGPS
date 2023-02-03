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
        decimalFormat = DecimalFormat("##.#")
        return decimalFormat.format(a).toString()
    }
    fun formatTVAcc(a: Float): String{
        decimalFormat = DecimalFormat("##.#")
        return decimalFormat.format(a).toString()
    }
}