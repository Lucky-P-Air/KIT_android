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
        it.asContact(it)
    }
}

// TODO: Potentially remove this messy method by moving its code into List<>.asContacts() above
fun DatabaseContact.asContact(databaseContact: DatabaseContact): Contact {
    return Contact(
        id = databaseContact.id,
        firstName = databaseContact.firstName,
        lastName = databaseContact.lastName,
        phoneNumber = databaseContact.phoneNumber,
        email = databaseContact.email,
        intervalTime = databaseContact.intervalTime,
        intervalUnit = databaseContact.intervalUnit,
        remindersEnabled = databaseContact.remindersEnabled,
        lastContacted = databaseContact.lastContacted?.let { it1 -> parseLocalDateTimes(it1) },
        createdAt = parseLocalDateTimes(databaseContact.createdAt),
        updatedAt = parseLocalDateTimes(databaseContact.updatedAt),
        status = databaseContact.status
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
