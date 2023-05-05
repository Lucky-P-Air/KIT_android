package com.example.kit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission

class FakeLocalDataSource : DataSourceInterface {
    // Mock database
    var fakeDatabase: HashMap<String, Contact> = HashMap()

    private val observableContacts = MutableLiveData<List<Contact>>()
    private val observableContact = MutableLiveData<Contact>()

    override fun observeContacts(): LiveData<List<Contact>> {
        return observableContacts
    }

    override fun observeContact(id: String): LiveData<Contact> {
        return observableContact
    }

    override suspend fun addContact(contactSubmission: ContactSubmission) {
        // NO-OP
    }

    override suspend fun deleteContact(contact: Contact) {
        fakeDatabase.remove(contact.id)
    }

    override suspend fun getContacts(): List<Contact> {
        refreshContacts()
        return observableContacts.value?: emptyList()
    }

    override suspend fun getContactDetail(contactID: String): Contact {
        observableContact.value = fakeDatabase[contactID]
        return observableContact.value!!
    }

    override suspend fun saveContact(contact: Contact): Contact {
        fakeDatabase[contact.id] = contact
        observableContact.value = contact
        return contact
    }

    override suspend fun refreshContacts() {
        observableContacts.value = fakeDatabase.values.toList()
    }
}