package com.example.kit.model

data class Contact(
    val firstName: String,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val intervalTime: Int,
    val intervalUnit: String) {
    // TODO: Overwrite null values for constructor parameters to avoid always writing null
}