package com.example.kit.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.example.kit.model.*
import com.example.kit.network.ContactRequest
import com.example.kit.network.contactPushFromContactAdapter
import com.example.kit.network.contactPushFromContactSubmissionAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

private const val TAG = "ContactRepository"
class ContactRepository(private val database: ContactsDatabase) {

    val dbContacts: LiveData<List<DatabaseContact>> = database.contactDao.getAllContacts().asLiveData()
    // Convert list of DbContacts to Contacts (Domain Model)
    val allContacts: LiveData<List<Contact>> = Transformations.map(dbContacts) {
            it.asContacts()
                .sortedWith(byFirstName)
    }

    suspend fun postContact(contactSubmission: ContactSubmission) {
        /**
         * Suspend function to POST a new ContactSubmission to the webserver for a new contact
         */
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Add Contact coroutine launched for $contactSubmission")
                //Serialize new contact information into JSON-ready object
                val contactPost =
                    ContactRequest(contactPushFromContactSubmissionAdapter(contactSubmission))
                Log.d(TAG, "ContactRequest: $contactPost")
                val response = async {
                    ContactApi.retrofitService.postNewContact(
                        Secrets().headers,
                        contactPost
                    )
                }
                Log.d(TAG,
                    "Add Contact coroutine SUCCESS for " +
                            "${response.await().data.attributes.firstName} " +
                            "with id# ${response.await().data.id}"
                )
                //_currentContact.postValue(contactFromEntryAdapter(response.await().data))
            } catch (e: Exception) {
                Log.d(TAG, "Exception occurred during Add Contact coroutine: ${e.message}")
            }
        }
    }

    suspend fun putContact(contact: Contact): Contact { // TODO: should probably return a Contact for updating currentContact in viewmodel
         /**
         * Suspend function to PUT a revised Contact to the webserver
         */
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG,"Initiating EditContact PUT request for $contact")
                //Serialize new contact information into JSON-ready object
                val contactPut = ContactRequest(contactPushFromContactAdapter(contact))
                Log.d(TAG, "ContentRequest(UpdateContact): $contactPut")
                val response = async {
                    ContactApi.retrofitService.updateContact(
                        contact.id,
                        Secrets().headers,
                        contactPut
                    )
                }
                Log.d(TAG,"PUT request successful? ${response.await().isSuccessful}")
                Log.d(TAG,"Response message from PUT request: ${response.await().message()}")
                Log.d(TAG,"Response from PUT request: ${response.await().body()!!.data}")
                return@withContext contactFromEntryAdapter(response.await().body()!!.data)
            } catch (e: Exception) {
                Log.d(TAG,"Exception occurred during updateContact operation: ${e.message}")
                return@withContext contact
            }
        }
    }

    suspend fun refreshContacts() {
        withContext(Dispatchers.IO) {
            try {
                val response = ContactApi.retrofitService.getContacts(Secrets().headers)
                Log.d(TAG,"Get request successful, retrieved ${response.data.size} entries."
                )
                database.contactDao.insertAll(response.asDatabaseContacts())
                Log.d(TAG,"Contact List inserted into database")
            } catch (e: java.lang.Exception) {
                Log.d(TAG, "Exception during refreshContacts coroutine: ${e.toString()}")
            }
        }
    }
    // TODO: synchronize database of contacts with returned HTTP response list. Delete any not present

    // TODO: UPDATE a single contact
    // TODO: query database for all contacts
    // TODO: query database for contacts with an overdue/upcoming status
    // TODO: query database for a specific contact
    // TODO: POST a newly added contact to server
    // TODO: DELETE a specific contact from server... then:
        // TODO: DELETE a specific contact from database



    // Sorting Method
    val byFirstName = Comparator.comparing<Contact, String> { contact: Contact ->
        contact.firstName.lowercase()
    }
}