package com.example.kit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission
import com.example.kit.utils.getTimeEpochString
import kotlinx.coroutines.runBlocking

class FakeTestRepository : RepositoryInterface {

    var contactsServiceData: LinkedHashMap<String, Contact> = LinkedHashMap()
    private val observableContacts = MutableLiveData<List<Contact>>()
    private val observableContact = MutableLiveData<Contact>()

    override val allContacts: LiveData<List<Contact>>
        get() = observableContacts

    private fun serializeContact(contactSubmission: ContactSubmission): Contact {
        val newId = (contactsServiceData.size + 100).toString()
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

    override suspend fun deleteContact(contact: Contact) {
        contactsServiceData.remove(contact.id)
    }

    override suspend fun fetchContactDetail(contactID: String) {
        observableContact.postValue(contactsServiceData[contactID])
    }

    override fun getContactDetail(contactID: String): LiveData<Contact> {
        runBlocking { fetchContactDetail(contactID) }
        return observableContact
    }

    override suspend fun postContact(contactSubmission: ContactSubmission) {
        val contact = serializeContact(contactSubmission)
        contactsServiceData[contact.id] = contact
    }

    override suspend fun putContact(contact: Contact) {
        contactsServiceData[contact.id] = contact
    }

    override suspend fun refreshContacts() {
        observableContacts.postValue(contactsServiceData.values.toList())
    }

}