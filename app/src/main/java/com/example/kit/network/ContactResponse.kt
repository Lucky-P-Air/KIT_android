package com.example.kit.model

import com.squareup.moshi.Json

// @JsonClass(generateAdapter = true)
data class ContactResponse(
    val data: List<ContactEntry>
)

// @JsonClass(generateAdapter = true)
data class ContactEntry(
    val type: String, // but generally just "Contact"
    val id: String,
    val attributes: Attributes
)

//@JsonClass(generateAdapter = true)
data class Attributes(
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "phone_number") val phoneNumber: String?,
    val email: String?,
    @Json(name = "reminders_enabled") val remindersEnabled: Boolean,
    @Json(name = "last_contacted") val lastContacted: String?,
    @Json(name = "interval_unit") val intervalUnit: String,
    @Json(name = "interval_number") val intervalNumber: Int,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    val status: String?
    )

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