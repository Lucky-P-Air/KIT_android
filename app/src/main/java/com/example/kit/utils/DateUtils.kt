package com.example.kit.utils

import com.example.kit.model.Contact
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

//class DateUtils {

fun asDate(localDate: LocalDate): Date {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
}

fun asDate(localDateTime: LocalDateTime): Date {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
}

fun asLocalDate(date: Date): LocalDate {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun asLocalDateTime(date: Date): LocalDateTime {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

fun formatLocalDates(dateValue: LocalDate): String {
    /** Return a String, formatted "MMM d, y", from an input LocalDate object
     * */
    val formatter = DateTimeFormatter.ofPattern("MMM d, y").withZone(ZoneId.systemDefault())
    return dateValue.format(formatter)
}

fun formatDates(dateValue: Date): String {
    /** Return a String, formatted "MMM d, y", from an input Date object
     * */
    val formatter = SimpleDateFormat("MMM d, y", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(dateValue)
}

fun parseDates(dateString: String): Date? {
    /** Return a Date? object from an input Datetime String that is based in UTC
     * */
    //val dateStringParser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.parse(dateString)
}

fun parseLocalDates(dateString: String): LocalDate {
    return asLocalDate(parseDates(dateString)!!)
}

fun getNextContactLocalDate(contact: Contact): LocalDate {
    /**
     * Takes input of a Contact object and returns a LocalDate for the next expected reminder
     * for that contact.
     *
     * Currently processes Contact date properties as DateStrings.
     * TODO Refactor function after converting Contact date-property-types
     */

    // LocalDates of createdAt and lastContact, generated from date String
    //val lastContactLocalDate = contact.lastContacted?.let { parseLocalDates(it) }
    //val createdLocalDate = parseLocalDates(contact.createdAt)
    // Use these 2 instead of 2 above after converting Contact date-property-types
    val lastContactLocalDate = contact.lastContacted
    val createdLocalDate = contact.createdAt

    // Set starting point for next reminderDate based on lastContact or createdAt
    var reminderDate = lastContactLocalDate ?: createdLocalDate
    // Increment next reminderDate
    when (contact.intervalUnit) {
        "days" -> reminderDate = reminderDate.plusDays(contact.intervalTime.toLong())
        "weeks" -> reminderDate = reminderDate.plusWeeks(contact.intervalTime.toLong())
        "months" -> reminderDate = reminderDate.plusMonths(contact.intervalTime.toLong())
        "years" -> reminderDate = reminderDate.plusYears(contact.intervalTime.toLong())
    }
    return reminderDate
}
//}