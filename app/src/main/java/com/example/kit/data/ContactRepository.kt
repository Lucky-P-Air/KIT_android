package com.example.kit.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission
import com.example.kit.model.asContacts
import com.example.kit.model.contactFromEntryAdapter
import com.example.kit.network.ContactRequest
import com.example.kit.network.contactPushFromContactAdapter
import com.example.kit.network.contactPushFromContactSubmissionAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

private const val TAG = "ContactRepository"
class ContactRepository(private val database: ContactsDatabase) {

    val allContacts: LiveData<List<Contact>> = database.contactDao.getAllContacts().asLiveData()
    private val ioDispatcher = Dispatchers.IO
    private val mainDispatcher = Dispatchers.Main

    suspend fun deleteContact(contact: Contact) {
        /**
         * Delete contact record from database & network server
         */
        val id = contact.id
        withContext(ioDispatcher) {
            // Delete from Network server
            try {
                Log.d(TAG, "Delete Contact coroutine launched for $id")
                ContactApi.retrofitService.deleteContact(
                    id,
                    Secrets().headers
                )

                // Delete from database
                database.contactDao.deleteContact(contact)
                //TODO: This potentially leaves room for a success DELETE http request, but unsuccessful database deletion?
                Log.d(TAG, "Delete Contact coroutine SUCCESS")
            } catch (e: Exception) {
                Log.d(TAG, "Exception occurred during Delete Contact coroutine: ${e.message}")
                throw e
            }
        }
    }

    suspend fun fetchContactDetail(contactID: String) {
        /**
         * GET request Contact Record for specific ID number and
         * insert it into application database
         */
        withContext(ioDispatcher) {
            try {
                val singleContactResponse = ContactApi.retrofitService.getSingleContact(
                    contactID,
                    Secrets().headers
                )
                database.contactDao.insertContact(
                    contactFromEntryAdapter(singleContactResponse.data)
                )
                withContext(mainDispatcher) {
                    Log.d(TAG,"Contact ${singleContactResponse.data} retrieved from API")
                }
            } catch (e: Exception) {
                Log.d(TAG,"Exception occurred during fetchContactDetail from API: ${e.message}")
            }
        }
    }

    fun getContactDetail(contactID: String): LiveData<Contact> {
        /**
         * Retrieve Contact Record for specific ID number and
         * Return Contact
         */
        Log.d(TAG, "Requesting $contactID from database")
        return database.contactDao.getContact(contactID).asLiveData()
    }

    suspend fun postContact(contactSubmission: ContactSubmission) {
        /**
         * Suspend function to POST a new ContactSubmission to the webserver for a new contact.
         * Catches the response contact and updates database
         */
        withContext(ioDispatcher) {
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
                //TODO: Change API response type to Response<ContactResponse> so .isSuccessful checking can occur
                Log.d(TAG,
                    "Add Contact coroutine SUCCESS for " +
                            "${response.await().data.attributes.firstName} " +
                            "with id# ${response.await().data.id}"
                )

                // Insert into database
                database.contactDao.insertContact(
                    contactFromEntryAdapter(response.await().data)
                )
                Log.d(TAG,
                    "Inserted into database: " +
                            "${response.await().data.attributes.firstName} " +
                            "with id# ${response.await().data.id}"
                )
            } catch (e: Exception) {
                Log.d(TAG, "Exception occurred during Add Contact coroutine: ${e.message}")
                throw e
            }
        }
    }

    suspend fun putContact(contact: Contact): Contact { // TODO: should probably return a Contact for updating currentContact in viewmodel
         /**
         * Suspend function to PUT a revised Contact to the webserver.
         * Catches the response contact and updates database
         */
        return withContext(ioDispatcher) {
            try {
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
                if (response.await().isSuccessful) {
                    with(response.await().body()!!) {
                        Log.d(TAG, "Response from PUT request: ${this.data}")

                        // Insert into database
                        database.contactDao.insertContact(contactFromEntryAdapter(this.data))
                        Log.d(TAG,
                            "Inserted into database: " +
                                    "${this.data.attributes.firstName} " +
                                    "with id# ${this.data.id}"
                        )
                        return@withContext contactFromEntryAdapter(this.data)
                }
                } else {
                    //Log.d(TAG,"Error response from PUT request: ${response.await().errorBody()}")
                    //return@withContext contact
                    throw Exception("PUT request was unsuccessful. ${response.await().message()}")
                }
            } catch (e: Exception) {
                Log.d(TAG,"Exception occurred during updateContact operation: ${e.message}")
                throw e
            }
        }
    }

    suspend fun refreshContacts() {
        withContext(ioDispatcher) {
            try {
                val response = ContactApi.retrofitService.getContacts(Secrets().headers)
                Log.d(TAG,"Get request successful, retrieved ${response.data.size} entries."
                )
                database.contactDao.insertAll(response.asContacts())
                Log.d(TAG,"Contact List inserted into database")
            } catch (e: Exception) {
                Log.d(TAG, "Exception during refreshContacts coroutine: $e")
            }
        }
    }
    // TODO: synchronize database of contacts with returned HTTP response list. Delete any not present

    // TODO: query database for contacts with an overdue/upcoming status


    // Sorting Method
    private val byFirstName = Comparator.comparing<Contact, String> { contact: Contact ->
        contact.firstName.lowercase()
    }
}