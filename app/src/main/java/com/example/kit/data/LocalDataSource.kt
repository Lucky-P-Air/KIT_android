package com.example.kit.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "LocalDataSource"

class LocalDataSource internal constructor(
    private val contactDao: ContactDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataSourceInterface {

    private val observableContacts = contactDao.observeAllContacts().asLiveData()
    private val observableContact = MutableLiveData<Contact>()

    override fun observeContacts(): LiveData<List<Contact>> {
        return observableContacts
    }

    override fun observeContact(id: String): LiveData<Contact> {
        // TODO: SHould be suspend fun in a coroutinescope?
        return contactDao.observeContact(id).asLiveData()
    }

    override suspend fun addContact(contactSubmission: ContactSubmission) {
        Log.i(TAG, "Nothing done locally. ContactSubmission type is not admissible to database")
    }

    override suspend fun deleteContact(contact: Contact) {
        withContext(ioDispatcher) {
            contactDao.deleteContact(contact)
        }
    }

    override suspend fun getContacts(): List<Contact> {
        return contactDao.getAllContacts()
    }

    override suspend fun getContactDetail(contactID: String): Contact {
        return withContext(ioDispatcher) {
            return@withContext contactDao.getContact(contactID)
        }
    }

    override suspend fun refreshContacts() {
        // Unnecessary with current implementation of observableContacts
        /*
        Log.d(TAG, "Refreshing contacts from database")
        withContext(ioDispatcher) {
            observableContacts.postValue(contactDao.getAllContacts())
        } */
    }

    override suspend fun saveContact(contact: Contact): Contact {
        withContext(ioDispatcher) {
            contactDao.insertContact(contact)
        }
        //TODO Remove the return of this contact
        return contact
    }
}