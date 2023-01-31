package com.example.kit.model

// TODO: Actually connect this class to a Datasource class (ContactSource) that builds Contacts
data class Contact(
    val firstName: String,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val intervalTime: Int,
    val intervalUnit: String) {
    // TODO: Overwrite null values for constructor parameters to avoid always writing null
}