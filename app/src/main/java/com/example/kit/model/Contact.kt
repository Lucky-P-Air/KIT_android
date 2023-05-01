package com.example.kit.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="contacts_table")
data class Contact(
    @PrimaryKey(autoGenerate = false) val id: String,
    @ColumnInfo("first_name") val firstName: String,
    @ColumnInfo("last_name") val lastName: String?,
    @ColumnInfo("phone_number") val phoneNumber: String?,
    @ColumnInfo val email: String?,
    @ColumnInfo("interval_time") val intervalTime: Int,
    @ColumnInfo("interval_unit") val intervalUnit: String,
    @ColumnInfo("reminders_enabled") val remindersEnabled: Boolean,
    @ColumnInfo("last_contacted") val lastContacted: String?,
    @ColumnInfo("created_at") val createdAt: String,
    @ColumnInfo("updated_at") val updatedAt: String,
    @ColumnInfo val status: String
)

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
     * Convert ContactEntry object into a Contact object with null-values replaced
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
        contactEntry.attributes.lastContacted,
        contactEntry.attributes.createdAt!!,
        contactEntry.attributes.updatedAt!!,
        contactEntry.attributes.status ?: "",
    )
}
