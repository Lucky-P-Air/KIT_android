package com.example.kit.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "ContactRepository"
class ContactRepository(
    private val contactsLocalDataSource: DataSourceInterface,
    private val contactsRemoteDataSource: DataSourceInterface,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RepositoryInterface {

    override val allContacts: LiveData<List<Contact>> = contactsLocalDataSource.observeContacts()

    override suspend fun deleteContact(contact: Contact) {
        /**
         * Delete contact record from database & network server
         */
        try {
            Log.d(TAG, "Delete Contact coroutine launched for ${contact.id}")
            contactsRemoteDataSource.deleteContact(contact)
            contactsLocalDataSource.deleteContact(contact)
            refreshContacts()
            //TODO: This potentially leaves room for a success DELETE http request, but unsuccessful database deletion?
            Log.d(TAG, "Delete Contact coroutine SUCCESS")
        } catch (e: Exception) {
            Log.d(TAG, "Exception occurred during Delete Contact coroutine: ${e.message}")
            throw e
        }
    }

    override suspend fun fetchContactDetail(contactID: String) {
        /**
         * GET request Contact Record for specific ID number and
         * insert it into application database
         */
        try {
            val freshContact = contactsRemoteDataSource.getContactDetail(contactID)
            contactsLocalDataSource.saveContact(freshContact)
            Log.d(TAG,"Contact $freshContact retrieved from API & saved to database")
        } catch (e: Exception) {
            Log.d(TAG,"Exception occurred during fetchContactDetail from API: ${e.message}")
        }
    }

    override fun getContactDetail(contactID: String): LiveData<Contact> {
        /**
         * Retrieve Contact Record for specific ID number and
         * Return Contact
         */
        Log.d(TAG, "Requesting $contactID from database")
        return contactsLocalDataSource.observeContact(contactID)
    }

    override suspend fun postContact(contactSubmission: ContactSubmission) {
        /**
         * Suspend function to POST a new ContactSubmission to the webserver for a new contact.
         * Then refresh contact list from network & database.
         */
        try {
            Log.d(TAG, "Add Contact coroutine launched for $contactSubmission")
            contactsRemoteDataSource.addContact(contactSubmission)
            //TODO: Change API response type to Response<ContactResponse> so .isSuccessful checking can occur
            refreshContacts()
            Log.d(TAG,"Add Contact coroutine SUCCESS")
        } catch (e: Exception) {
            Log.d(TAG, "Exception occurred during Add Contact coroutine: ${e.message}")
            throw e
        }
    }

    override suspend fun putContact(contact: Contact) {
         /**
         * Suspend function to PUT a revised Contact to the webserver.
         * Catches the response contact and updates database
         */
        withContext(ioDispatcher) {
            try {
                val responseContact = contactsRemoteDataSource.saveContact(contact)
                Log.d(TAG,"PUT request successful")
                contactsLocalDataSource.saveContact(responseContact)
            } catch (e: Exception) {
                Log.d(TAG,"Exception occurred during putContact operation: ${e.message}")
                throw e
            }
        }
    }

    override suspend fun refreshContacts() {
        /**
         * Refresh contact list from network, then insert all contact records into database
         */
        try {
            contactsRemoteDataSource.refreshContacts()
            // Insert all contacts into database
            contactsRemoteDataSource.getContacts().map {contact: Contact ->
                contactsLocalDataSource.saveContact(contact)
            }
            Log.d(TAG,"Contact List inserted into database")
        } catch (e: Exception) {
            Log.d(TAG, "Exception during refreshContacts coroutine: $e")
        }
    }
    // TODO: synchronize database of contacts with returned HTTP response list. Delete any not present

    // TODO: query database for contacts with an overdue/upcoming status


    /*// Sorting Method
    private val byFirstName = Comparator.comparing<Contact, String> { contact: Contact ->
        contact.firstName.lowercase()
    }
     */
}