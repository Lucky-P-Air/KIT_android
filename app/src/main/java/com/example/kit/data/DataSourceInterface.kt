package com.example.kit.data

import androidx.lifecycle.LiveData
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission

interface DataSourceInterface {

    fun observeContacts(): LiveData<List<Contact>>
    fun observeContact(id: String): LiveData<Contact>

    suspend fun addContact(contactSubmission: ContactSubmission)

    suspend fun deleteContact(contact: Contact)

    suspend fun getContacts(): List<Contact>
    suspend fun getContactDetail(contactID: String): Contact

    suspend fun saveContact(contact: Contact): Contact

    suspend fun refreshContacts()
}