package com.example.KYL.constans

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.util.*

object MainTime {
    // Функции получения текущего времени для разных форматов
    fun getTime(): String {
        val formatter = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    fun getTimeForSaveFile(): String {
        val formatter = SimpleDateFormat("dd_MM_yyyy HH_mm_ss", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

}