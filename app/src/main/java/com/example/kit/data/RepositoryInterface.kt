package com.example.kit.data

import androidx.lifecycle.LiveData
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission

interface RepositoryInterface {
    val allContacts: LiveData<List<Contact>>

    suspend fun deleteContact(contact: Contact)

    suspend fun fetchContactDetail(contactID: String)
    fun getContactDetail(contactID: String): LiveData<Contact>

    suspend fun postContact(contactSubmission: ContactSubmission)

    suspend fun putContact(contact: Contact)

    suspend fun refreshContacts()
}