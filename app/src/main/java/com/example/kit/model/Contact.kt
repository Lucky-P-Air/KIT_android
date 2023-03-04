package com.example.kit.model

import com.example.kit.utils.parseLocalDates
import java.time.LocalDate

// Starter class with just the properties needed to create/add a contact. Used in API POST
data class Contact(
    val id: String,
    val firstName: String,
    val lastName: String?,
    val phoneNumber: String?,
    val email: String?,
    val intervalTime: Int,
    val intervalUnit: String,
    val remindersEnabled: Boolean,
    val lastContacted: LocalDate?, //String?, //Datetime
    val createdAt: LocalDate, // String, // Datetime
    val updatedAt: LocalDate, //String, // Datetime
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
    //val lastContacted: LocalDate?, // not required for submissions
    val intervalUnit: String,
    val intervalTime: Int
    )
    // TODO: Overwrite null values for constructor parameters to avoid always writing null

fun contactFromEntryAdapter(contactEntry: ContactEntry) : Contact {
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
        contactEntry.attributes.lastContacted?.let { it1 -> parseLocalDates(it1) },
        parseLocalDates(contactEntry.attributes.createdAt!!),
        parseLocalDates(contactEntry.attributes.updatedAt!!),
        contactEntry.attributes.status ?: "",
    )
}


/* POSTing contacts will require this form:
Could be a ContactEntry if id: String is converted to String? (Not used in POST/PUSH body, rather, its used in the path)
{"data": {
    "attributes": {
        "first_name": "matt",
        "last_name": "pereira", OR null / absent
        "phone_number": "+13018732741", OR null / absent
        "email": "user@example.com", OR null / absent
        "reminders_enabled": true, OR null / absent
        "last_contacted_at": null, OR null / absent
        "interval_unit": "days",
        "interval_number": 7
    },
    "type":"Contact"
    }
}
 */