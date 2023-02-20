package com.example.kit.model

// Starter class with just the properties needed to create/add a contact. Used in API POST
data class Contact(
    val firstName: String,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val intervalTime: Int,
    val intervalUnit: String) {
    // TODO: Overwrite null values for constructor parameters to avoid always writing null
}

// Fuller Class with all the attributes expected from an API GET
/*data class ContactComplete(
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
)*/