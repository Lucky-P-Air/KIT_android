package com.example.kit.model

import java.time.LocalDate

// Starter class with just the properties needed to create/add a contact. Used in API POST
data class Contact(
    val id: String,
    val firstName: String,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val intervalTime: Int,
    val intervalUnit: String,
    val reminderEnabled: Boolean,
    val lastContacted: LocalDate?, //String?, //Datetime
    val createdAt: LocalDate, // String, // Datetime
    val updatedAt: LocalDate, //String, // Datetime
    val status: String
    )
// TODO: Overwrite null values for constructor parameters to avoid always writing null

data class ContactSubmission(
    val firstName: String,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val intervalTime: Int,
    val intervalUnit: String) {
    // TODO: Overwrite null values for constructor parameters to avoid always writing null
}