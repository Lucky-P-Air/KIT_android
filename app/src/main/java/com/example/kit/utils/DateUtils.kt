package com.example.kit.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class DateUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun asDate(localDate: LocalDate): Date {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun asDate(localDateTime: LocalDateTime): Date {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun asLocalDate(date: Date): LocalDate {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun asLocalDateTime(date: Date): LocalDateTime {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }
}