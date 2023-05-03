package com.example.kit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission
import com.example.kit.utils.getTimeNowString
import kotlinx.coroutines.runBlocking

class FakeTestRepository : RepositoryInterface {

    var contactsServiceData: LinkedHashMap<String, Contact> = LinkedHashMap()
    private val observableContacts = MutableLiveData<List<Contact>>()
    private val observableContact = MutableLiveData<Contact>()

    override val allContacts: LiveData<List<Contact>>
        get() = observableContacts

    private fun serializeContact(contactSubmission: ContactSubmission): Contact {
        val newId = (contactsServiceData.size + 100).toString()
        val timeNow = getTimeNowString()
        return with (contactSubmission) {
            val contact = Contact(
                id=newId,
                this.firstName,
                this.lastName,
                this.phoneNumber,
                this.email,
                this.intervalTime,
                this.intervalUnit,
                this.remindersEnabled,
                timeNow,
                timeNow,
                timeNow,
                "Overdue"
            )
            return@with contact
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