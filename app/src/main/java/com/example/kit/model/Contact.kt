package com.example.kit.model

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
    val lastContacted: String?, //Datetime
    val createdAt: String, // Datetime
    val updatedAt: String, // Datetime
    val status: String?
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