package com.example.kit.utils

import com.example.kit.model.Contact
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

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

fun asLocalDateTimeFromUtc(date: Date): LocalDateTime {
    /** Convert a Date object to LocalDateTime in the timezone of UTC
     */
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.of("UTC"))
        .toLocalDateTime()
}

fun formatLocalDateTimes(dateValue: LocalDateTime): String {
    /** Return a String, formatted "MMM d, y", from an input LocalDateTime (UTC) object.
     *  Output timezone is local to the device.
     * */
    val formatter = DateTimeFormatter.ofPattern("MMM d, y")
        .withZone(ZoneId.systemDefault()) // format the output using this timezone
    return dateValue.atZone(ZoneId.of("UTC"))
        .format(formatter) // .atZone dictates the timezone of the input(dateValue)
}
fun formatDateStrings(dateString: String): String {
    /** Return a String, formatted "MMM d, y", from an input datetime String (UTC timezone)
     *  Output timezone is local to the device.
     * */
    return formatLocalDateTimes( parseLocalDateTimes(dateString) )
}

fun formatLocalDateTimesToUtc(dateValue: LocalDateTime): String {
    /** Return a datestring, formatted "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
     * from an input LocalDateTime object, with the timezone of the output
     * being UTC
     * */
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        .withZone(ZoneId.of("UTC"))
    return dateValue.atZone(ZoneId.of("UTC")).format(formatter)
}

fun getTimeNowString(): String {
    /**
     * Return the current time as a datestring, formatted "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
     * */
    return formatLocalDateTimesToUtc(
        Instant.now().atZone(ZoneId.of("UTC"))
            .toLocalDateTime())
}

fun formatDates(dateValue: Date): String {
    /** Return a String, formatted "MMM d, y", from an input Date object.
     * Time zone displayed is local to the device
     * */
    val formatter = SimpleDateFormat("MMM d, y", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(dateValue)
}

fun parseDates(dateString: String): Date? {
    /** Return a Date? object (local timezone) from an input Datetime String (UTC timezone)
     */
    //val dateStringParser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    //Log.d("ContactDateUtil", "parseDates returned ${formatter.parse(dateString)}")
    return formatter.parse(dateString)
}

fun parseLocalDateTimes(dateString: String): LocalDateTime {
    /** Return a LocalDateTime object (in timezone of UTC) from an input datestring,
     * <String> of this pattern : "yyyy-MM-ddTHH:mm:ss.SSSSSSZ"
     */
    //Log.d("ContactDateUtil", "parseLocalDateTimes (from UTC) returned ${asLocalDateTimeFromUtc(parseDates(dateString)!!)}")
    return asLocalDateTimeFromUtc(parseDates(dateString)!!)
}
//TODO: Maybe switch this output to String?
fun getNextContactLocalDateTime(contact: Contact): LocalDateTime {
    /** Takes input of a Contact object and returns a LocalDateTime (UTC timezone) for the
     * next expected reminder for that contact.
     *
     * Currently processes Contact date properties as DateStrings.
     */

    val lastContactLocalDate = contact.lastContacted
    val createdLocalDate = contact.createdAt

    // Set starting point for next reminderDate based on lastContact or createdAt
    var reminderDate = parseLocalDateTimes(
        lastContactLocalDate ?: createdLocalDate
    )

    // Increment next reminderDate
    with(contact.intervalTime.toLong()) {
        when (contact.intervalUnit) {
            "days" -> reminderDate = reminderDate.plusDays(this)
            "weeks" -> reminderDate = reminderDate.plusWeeks(this)
            "months" -> reminderDate = reminderDate.plusMonths(this)
            "years" -> reminderDate = reminderDate.plusYears(this)
        }
    }
    return reminderDate
}
