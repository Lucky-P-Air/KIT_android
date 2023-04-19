package com.example.kit.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kit.utils.parseLocalDateTimes

@Entity(tableName="contacts_table")
data class DatabaseContact(
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

fun List<DatabaseContact>.asContacts(): List<Contact> {
    return map {
        it.asContact()
    }
}

// TODO: Potentially remove this messy method by moving its code into List<>.asContacts() above
fun DatabaseContact.asContact(): Contact {
    return Contact(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        intervalTime = this.intervalTime,
        intervalUnit = this.intervalUnit,
        remindersEnabled = this.remindersEnabled,
        lastContacted = this.lastContacted?.let { it1 -> parseLocalDateTimes(it1) },
        createdAt = parseLocalDateTimes(this.createdAt),
        updatedAt = parseLocalDateTimes(this.updatedAt),
        status = this.status
    )
}

fun databaseContactFromEntryAdapter(contactEntry: ContactEntry): DatabaseContact {
    /**
     * Convert ContactEntry object into a Contact object with LocalDates and null-values replaced
     * by empty strings.
     *
     * Params: <ContactEntry>
     * Returns <DatabaseContact>
     */
    return DatabaseContact(
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
