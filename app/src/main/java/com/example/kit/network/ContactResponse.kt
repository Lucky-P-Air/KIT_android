package com.example.kit.model

import com.squareup.moshi.Json

// @JsonClass(generateAdapter = true)
data class ContactListResponse(
    val data: List<ContactEntry>
)
//@JsonClass(generateAdapter = true)
data class ContactResponse(
    val data: ContactEntry
)

// @JsonClass(generateAdapter = true)
data class ContactEntry(
    val type: String, // but generally just "Contact"
    val id: String?,
    val attributes: Attributes
)

//@JsonClass(generateAdapter = true)
data class Attributes(
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "phone_number") val phoneNumber: String?,
    val email: String?,
    @Json(name = "reminders_enabled") val remindersEnabled: Boolean,
    @Json(name = "last_contacted_at") val lastContacted: String?,
    @Json(name = "interval_unit") val intervalUnit: String,
    @Json(name = "interval_number") val intervalNumber: Int,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "updated_at") val updatedAt: String?,
    val status: String?
    )

/*
fun entryFromContactSubmissionAdapter(contactSubmission: ContactSubmission) : ContactEntry {
    /**
     * Convert ContactSubmission objects (with NO id) into a ContactEntry suitable for HTTP POSTs
     *
     * Params: <ContactSubmission>
     * Returns <ContactEntry>
     */
    fun attributesFromContactSubmission(contactSubmission: ContactSubmission) : Attributes {
        return Attributes(
            contactSubmission.firstName,
            if (contactSubmission.lastName.isNullOrEmpty()) null else contactSubmission.lastName,
            if (contactSubmission.phoneNumber.isNullOrEmpty()) null else contactSubmission.phoneNumber,
            if (contactSubmission.email.isNullOrEmpty()) null else contactSubmission.email,
            true,
            null,
            contactSubmission.intervalUnit,
            contactSubmission.intervalTime,
            null,
            null,
            null)
    }
    return ContactEntry(
        type = "Contact",
        id = null,
        attributes = attributesFromContactSubmission(contactSubmission)
    )
}

fun entryFromContactAdapter(contact: Contact) : ContactEntry {
    /**
     * Convert Contact object into a ContactEntry object with null-values and strings as required
     *
     * Params: <Contact>
     * Returns <ContactEntry>
     */
    fun attributesFromContact(contact: Contact) : Attributes {
        return Attributes(
            contact.firstName,
            if (contact.lastName.isNullOrEmpty()) null else contact.lastName,
            if (contact.phoneNumber.isNullOrEmpty()) null else contact.phoneNumber,
            if (contact.email.isNullOrEmpty()) null else contact.email,
            contact.reminderEnabled,
            contact.lastContacted?.let {it -> formatLocalDatesToUtc(it) },
            contact.intervalUnit,
            contact.intervalTime,
            formatLocalDatesToUtc(contact.createdAt),
            formatLocalDatesToUtc(contact.updatedAt),
            if (contact.status.isNullOrEmpty()) null else contact.status)
    }

    return ContactEntry(
        type = "Contact",
        id = contact.id,
        attributes = attributesFromContact(contact)
    )
}
*/
/** Optional reformatting of the nested structure of the API contact response
    into the simpler Contact and Complete Contact Models currently implemented
 */
/*
object ApiResponseMapper {
    fun contactListFromApi(response: ContactResponse): ContactComplete {
        return ContactComplete(
            response.id,
            response.attributes.firstName,
            response.attributes.lastName,
            response.attributes.phoneNumber,
            response.attributes.email,
            response.attributes.remindersEnabled,
            response.attributes.lastContacted,
            response.attributes.intervalUnit,
            response.attributes.intervalNumber,
            response.attributes.createdAt,
            response.attributes.updatedAt,
            response.attributes.status
        )
    }

    fun contactFromApi(list: List)
}
 */