package com.example.kit.model

import com.example.kit.utils.parseLocalDateTimes
import java.time.LocalDateTime

data class Contact(
    val id: String,
    val firstName: String,
    val lastName: String?,
    val phoneNumber: String?,
    val email: String?,
    val intervalTime: Int,
    val intervalUnit: String,
    val remindersEnabled: Boolean,
    val lastContacted: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val status: String
)

// TODO: Overwrite null values for constructor parameters to avoid always writing null

data class ContactSubmission(
    // Contact Submission will only be used for POSTs / adding new contacts
    val firstName: String,
    val lastName: String?,
    val phoneNumber: String?,
    val email: String?,
    val remindersEnabled: Boolean,
    //val lastContacted: LocalDateTime?, // not required for POST submissions
    val intervalUnit: String,
    val intervalTime: Int
)

fun contactFromEntryAdapter(contactEntry: ContactEntry): Contact {
    /**
     * Convert ContactEntry object into a Contact object with LocalDates and null-values replaced
     * by empty strings.
     *
     * Params: <ContactEntry>
     * Returns <Contact>
     */
    return Contact(
        contactEntry.id!!,
        contactEntry.attributes.firstName,
        contactEntry.attributes.lastName ?: "",
        contactEntry.attributes.phoneNumber ?: "",
        contactEntry.attributes.email ?: "",
        contactEntry.attributes.intervalNumber,
        contactEntry.attributes.intervalUnit,
        contactEntry.attributes.remindersEnabled,
        contactEntry.attributes.lastContacted?.let { it1 -> parseLocalDateTimes(it1) },
        parseLocalDateTimes(contactEntry.attributes.createdAt!!),
        parseLocalDateTimes(contactEntry.attributes.updatedAt!!),
        contactEntry.attributes.status ?: "",
    )
}
