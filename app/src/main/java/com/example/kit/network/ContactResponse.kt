package com.example.kit.model

import com.squareup.moshi.Json

// @JsonClass(generateAdapter = true)
data class ContactListResponse(
    val data: List<ContactEntry>
)

/**
 * Convert Network results to database objects
 */
fun ContactListResponse.asDatabaseContacts(): List<DatabaseContact> {
    return data.map {
        databaseContactFromEntryAdapter(it)
    }
}

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
