package com.example.kit.utils

import com.example.kit.model.Contact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.time.*

internal class DateUtilsTest {

    @Test
    fun formatDateStrings_nominal_equals() {
        /**
         * Parse an input datestring, formatted "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" in UTC zone,
         * and re-format it to readable format, "MMM d, y" in device's local timezone (US-MT)
         */
        // GIVEN a specific date as a datestring
        val date1 = "2023-05-06T22:14:02.100000Z"
        val date2 = "1967-02-28T06:59:00.000000Z" // Pre-Epoch. Before 7am
        val date3 = "1967-02-28T07:59:00.000000Z" // Pre-Epoch. After 7am

        // WHEN the formatDateStrings is called on it
        val result1 = formatDateStrings(date1)
        val result2 = formatDateStrings(date2)
        val result3 = formatDateStrings(date3)

        // The results are offset 7 hours back from UTC timezone.
        // Times before 07:00am register as the previous day in local (US-MT) zone
        assertEquals("May 6, 2023", result1)
        assertEquals("Feb 27, 1967", result2)
        assertEquals("Feb 28, 1967", result3)
    }

    @Test
    fun formatDateStrings_falseDate_notEquals() {
        /**
         * Parse an invalid date (e.g. February 30) input datestring,
         * formatted "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" in UTC zone,
         * and formatted result is not the invalid date
         */
        // GIVEN an invalid date as a datestring
        val date1 = "1967-02-30T12:59:00.000000Z" // Fake date

        // WHEN the formatDateStrings is called on it
        val result1 = formatDateStrings(date1)

        // The results are offset 7 hours back from UTC timezone.
        assertNotEquals("Feb 30, 1967", result1)
    }

    @Test
    fun formatLocalDateTimes_nominal_equals() {
        /**
         * Format a LocalDateTime input regardless of original timezone,
         * to readable format, "MMM d, y" in device's local timezone (US-MT)
         */
        // GIVEN a LocalDateTime (in UTC zone, or Local Zone)
        val date1 = LocalDateTime.of(2010, Month.FEBRUARY, 28, 5, 59) // UTC zone
        val date2 = LocalDateTime.of(2010, Month.FEBRUARY, 28, 15, 0)
            .atZone(ZoneId.systemDefault()) // Local device timezone
            .toLocalDateTime()
        // WHEN the formatLocalDateTimes is called on it
        val result1 = formatLocalDateTimes(date1)
        val result2 = formatLocalDateTimes(date2)
        // The resulting readable date is in the device's local zone (US-MT)
        assertEquals("Feb 27, 2010", result1)
        assertEquals("Feb 28, 2010", result2)
    }

    @Test
    fun formatLocalDateTimesToUtc_nominal_equals() {
        /**
         * Format a LocalDateTime input to datestring format,
         * "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" in UTC zone
         */
        // GIVEN a LocalDateTime (in UTC zone, or Local Zone)
        val date1 = LocalDateTime.of(2010, Month.FEBRUARY, 28, 5, 59) // UTC zone
        val date2 = LocalDateTime.of(2010, Month.FEBRUARY, 28, 15, 0)
            .atZone(ZoneId.systemDefault()) // Local device timezone
            .toLocalDateTime()
        // WHEN the formatLocalDateTimes is called on it
        val result1 = formatLocalDateTimesToUtc(date1)
        val result2 = formatLocalDateTimesToUtc(date2)
        // The resulting readable date is in the device's local zone (US-MT)
        assertEquals("2010-02-28T05:59:00.000000Z", result1)
        assertEquals("2010-02-28T15:00:00.000000Z", result2)
    }

    @Test
    fun getTimeEpoch_nominal_equals() {
        val expected = LocalDateTime.of(1970, Month.JANUARY, 1,0,0)
        val result = getTimeEpoch()
        assertEquals(expected, result)
    }

    @Test
    fun getTimeEpochString_nominal_equals() {
        val expected = "1970-01-01T00:00:00.000000Z"
        val result = getTimeEpochString()
        assertEquals(expected, result)
    }

    @Test
    fun parseDates_epochDateStringToDate_equals() {
        val timeString = "1970-01-01T00:00:00.000000Z"
        val result = parseDates(timeString)!!
        assertEquals(Instant.EPOCH, result.toInstant())

    }

    @Test
    fun parseLocalDateTimes_epochDateStringToLocalDateTimeUtc_equals() {
        // GIVEN the Epoch as a date string
        val epochString = "1970-01-01T00:00:00.000000Z"
        // WHEN parseLocalDateTimes is called on it
        val result = parseLocalDateTimes(epochString)
        // THEN the result equals the LocalDateTime of the epoch
        val epochLocalDateTime = LocalDateTime.ofEpochSecond(0,0, ZoneOffset.UTC)
        assertEquals( epochLocalDateTime, result)
    }

    @Test
    fun getNextContactLocalDateTime_withLastContacted_equals() {
        // GIVEN contacts with "date created" and "date last contacted" entries, and various reminder intervals
        val created = "2018-06-15T12:00:00.000000Z"
        val lastContacted = "2019-02-15T12:00:00.000000Z"
        val contact3days =   Contact("1", "Michael", "Scott", null, null, 3, "days", true, lastContacted, created, lastContacted, "overdue")
        val contact2weeks =  Contact("1", "Michael", "Scott", null, null, 2, "weeks", true, lastContacted, created, lastContacted, "overdue")
        val contact4months = Contact("1", "Michael", "Scott", null, null, 4, "months", true, lastContacted, created, lastContacted, "overdue")
        val contact1year =   Contact("1", "Michael", "Scott", null, null, 1, "years", true, lastContacted, created, lastContacted, "overdue")
        val contact2weeksLeapYear =  Contact("1", "Michael", "Scott", null, null, 2, "weeks", true, lastContacted, created, lastContacted, "overdue")
        // WHEN "Next Contact" dates are calculated
        val result3days = getNextContactLocalDateTime(contact3days)
        val result2weeks = getNextContactLocalDateTime(contact2weeks)
        val result4months = getNextContactLocalDateTime(contact4months)
        val result1year = getNextContactLocalDateTime(contact1year)
        val result2weeksLeapYear = getNextContactLocalDateTime(contact2weeksLeapYear)
        // THEN they will match the expected reminder interval from last contact date
        assertEquals(parseLocalDateTimes("2019-02-18T12:00:00.000000Z"), result3days)
        assertEquals(parseLocalDateTimes("2019-03-01T12:00:00.000000Z"), result2weeks)
        assertEquals(parseLocalDateTimes("2019-06-15T12:00:00.000000Z"), result4months)
        assertEquals(parseLocalDateTimes("2020-02-15T12:00:00.000000Z"), result1year)
        assertEquals(parseLocalDateTimes("2019-02-29T12:00:00.000000Z"), result2weeksLeapYear)
    }

    @Test
    fun getNextContactLocalDateTime_nullLastContacted_equals() {
        // GIVEN contacts with "date created" entry and NO "date last contacted" date, and various reminder intervals
        val created = "2018-06-15T12:00:00.000000Z"
        val lastContacted = "2019-02-15T12:00:00.000000Z"
        val contact3days =   Contact("1", "Michael", "Scott", null, null, 3, "days", true, null, created, lastContacted, "overdue")
        val contact2weeks =  Contact("1", "Michael", "Scott", null, null, 2, "weeks", true, null, created, lastContacted, "overdue")
        val contact4months = Contact("1", "Michael", "Scott", null, null, 4, "months", true, null, created, lastContacted, "overdue")
        val contact1year =   Contact("1", "Michael", "Scott", null, null, 1, "years", true, null, created, lastContacted, "overdue")
        // WHEN "Next Contact" dates are calculated
        val result3days = getNextContactLocalDateTime(contact3days)
        val result2weeks = getNextContactLocalDateTime(contact2weeks)
        val result4months = getNextContactLocalDateTime(contact4months)
        val result1year = getNextContactLocalDateTime(contact1year)
        // THEN they will match the expected reminder interval from created date
        assertEquals(parseLocalDateTimes("2018-06-18T12:00:00.000000Z"), result3days)
        assertEquals(parseLocalDateTimes("2018-06-29T12:00:00.000000Z"), result2weeks)
        assertEquals(parseLocalDateTimes("2018-10-15T12:00:00.000000Z"), result4months)
        assertEquals(parseLocalDateTimes("2019-06-15T12:00:00.000000Z"), result1year)
    }
}