package com.example.copernisea.map.utils

import java.util.*

object DateUtils {

    fun formatDate(millis: Long): String {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = millis
        return "${calendar.get(Calendar.YEAR)}-${formatMonth(calendar.get(Calendar.MONTH))}-${
            addZero(calendar.get(Calendar.DAY_OF_MONTH))
        }"
    }

    private fun formatMonth(month: Int): String = addZero(month + 1) //January is 0

    private fun addZero(value: Int): String = if (value < 10) {
        "0$value"
    } else {
        "$value"
    }
}