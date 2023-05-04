package com.example.kit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission
import com.example.kit.utils.getTimeEpochString

class FakeRemoteDataSource(var fakeDatabase: HashMap<String, Contact> = HashMap()) : DataSourceInterface {

    private val observableContacts = MutableLiveData<List<Contact>>()
    private val observableContact = MutableLiveData<Contact>()

    private fun serializeContact(contactSubmission: ContactSubmission): Contact {
        val newId = (fakeDatabase.size + 100).toString()
        val timeEpoch = getTimeEpochString()
        return with (contactSubmission) {
            return@with Contact(
                id = newId,
                firstName,
                lastName,
                phoneNumber,
                email,
                intervalTime,
                intervalUnit,
                true,
                timeEpoch,
                timeEpoch,
                timeEpoch,
                "overdue"
            )
        }
    }

    override fun observeContacts(): LiveData<List<Contact>> {
        return observableContacts
    }

    override fun observeContact(id: String): LiveData<Contact> {
        return observableContacts.map {contacts ->
            return@map (contacts.firstOrNull { contact: Contact ->  contact.id == id}!!)
            // TODO: May have null pointer exceptions after deleting a contact if this method is used
        }
    }

    override suspend fun addContact(contactSubmission: ContactSubmission) {
        val contact = serializeContact(contactSubmission)
        fakeDatabase[contact.id] = contact
    }

    override suspend fun deleteContact(contact: Contact) {
        fakeDatabase.remove(contact.id)
    }

    override suspend fun getContacts(): List<Contact> {
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