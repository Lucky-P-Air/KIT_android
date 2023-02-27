package com.example.kit.network

import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission
import com.squareup.moshi.Json

/**
 * Data Classes used for pushing data to server via HTTP Requests.
 * They contain a subset of the properties received from server responses,
 * represented in ContactResponse.kt
 */

data class ContactRequest(
    val data: ContactPush
)

// @JsonClass(generateAdapter = true)
data class ContactPush(
    val type: String, // but generally just "Contact"
    val id: String?,
    val attributes: PushAttributes
)

//@JsonClass(generateAdapter = true)
data class PushAttributes(
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "phone_number") val phoneNumber: String?,
    val email: String?,
    @Json(name = "reminders_enabled") val remindersEnabled: Boolean,
    // @Json(name = "last_contacted_at") val lastContacted: String?, //TODO uncomment this after Datetime fix
    @Json(name = "interval_unit") val intervalUnit: String,
    @Json(name = "interval_number") val intervalNumber: Int
)

fun contactPushFromContactSubmissionAdapter(contactSubmission: ContactSubmission) : ContactPush {
    /**
     * Convert ContactSubmission objects (with NO id) into a ContactPush suitable for HTTP POST/PUT
     *
     * Params: <ContactSubmission>
     * Returns <ContactPush>
     */
    fun pushAttributesFromContactSubmission(contactSubmission: ContactSubmission) : PushAttributes {
        return PushAttributes(
            contactSubmission.firstName,
            if (contactSubmission.lastName.isNullOrEmpty()) null else contactSubmission.lastName,
            if (contactSubmission.phoneNumber.isNullOrEmpty()) null else contactSubmission.phoneNumber,
            if (contactSubmission.email.isNullOrEmpty()) null else contactSubmission.email,
            true,
            // null, //TODO uncomment this after Datetime fix
            contactSubmission.intervalUnit,
            contactSubmission.intervalTime)
    }
    return ContactPush(
        type = "Contact",
        id = null,
        attributes = pushAttributesFromContactSubmission(contactSubmission)
    )
}

fun contactPushFromContactAdapter(contact: Contact) : ContactPush {
    /**
     * Convert Contact object into a ContactPush object with null-values and strings as required
     *
     * Params: <Contact>
     * Returns <ContactPush>
     */
    fun pushAttributesFromContact(contact: Contact) : PushAttributes {
        return PushAttributes(
            contact.firstName,
            if (contact.lastName.isNullOrEmpty()) null else contact.lastName,
            if (contact.phoneNumber.isNullOrEmpty()) null else contact.phoneNumber,
            if (contact.email.isNullOrEmpty()) null else contact.email,
            contact.reminderEnabled,
            //contact.lastContacted?.let {it -> formatLocalDatesToUtc(it) }, //TODO uncomment this
            contact.intervalUnit,
            contact.intervalTime)
    }

    return ContactPush(
        type = "Contact",
        id = contact.id,
        attributes = pushAttributesFromContact(contact)
    )
}