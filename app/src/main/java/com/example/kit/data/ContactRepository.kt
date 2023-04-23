package com.example.kit.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.example.kit.model.*
import com.example.kit.network.ContactRequest
import com.example.kit.network.contactPushFromContactAdapter
import com.example.kit.network.contactPushFromContactSubmissionAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

private const val TAG = "ContactRepository"
class ContactRepository(private val database: ContactsDatabase) {

    private val dbContacts: LiveData<List<DatabaseContact>> = database.contactDao.getAllContacts().asLiveData()
    // Convert list of DbContacts to Contacts (Domain Model)
    val allContacts: LiveData<List<Contact>> = Transformations.map(dbContacts) {
            it.asContacts()
                .sortedWith(byFirstName)
    }

    suspend fun deleteContact(dbContact: DatabaseContact) {
        /**
         * Delete contact record from database & network server
         */
        val id = dbContact.id
        withContext(Dispatchers.IO) {
            // Delete from Network server
            try {
                Log.d(TAG, "Delete Contact coroutine launched for $id")
                ContactApi.retrofitService.deleteContact(
                    id,
                    Secrets().headers
                )

                // Delete from database
                database.contactDao.deleteContact(dbContact)
                Log.d(TAG, "Delete Contact coroutine SUCCESS")
            } catch (e: Exception) {
                Log.d(TAG, "Exception occurred during Delete Contact coroutine: ${e.message}")
            }
        }
    }

    suspend fun fetchContactDetail(contactID: String) {
        /**
         * GET request Contact Record for specific ID number and
         * insert it into application database
         */
        withContext(Dispatchers.IO) {
            try {
                val singleContactResponse = ContactApi.retrofitService.getSingleContact(
                    contactID,
                    Secrets().headers
                )
                database.contactDao.insertContact(
                    databaseContactFromEntryAdapter(singleContactResponse.data)
                )
                withContext(Dispatchers.Main) {
                    Log.d(TAG,"Contact ${singleContactResponse.data} retrieved from API")
                }
            } catch (e: Exception) {
                Log.d(TAG,"Exception occurred during fetchContactDetail from API: ${e.message}")
            }
        }
    }

    fun getContactDetail(contactID: String): LiveData<Contact> {
        /**
         * Retrieve DatabaseContact Record for specific ID number and
         * Return Contact
         */
        Log.d(TAG, "Requesting $contactID from database")
        return getDatabaseContact(contactID).map {it.asContact()}
    }

    fun getDatabaseContact(contactID: String): LiveData<DatabaseContact> {
        //val contactData = withContext(Dispatchers.IO) {
        return database.contactDao.getContact(contactID).asLiveData()
    }

    suspend fun postContact(contactSubmission: ContactSubmission) {
        /**
         * Suspend function to POST a new ContactSubmission to the webserver for a new contact.
         * Catches the response contact and updates database
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
                //TODO: Change API response type to Response<ContactResponse> so .isSuccessful checking can occur
                Log.d(TAG,
                    "Add Contact coroutine SUCCESS for " +
                            "${response.await().data.attributes.firstName} " +
                            "with id# ${response.await().data.id}"
                )

                // Insert into database
                database.contactDao.insertContact(
                    databaseContactFromEntryAdapter(response.await().data)
                )
                Log.d(TAG,
                    "Inserted into database: " +
                            "${response.await().data.attributes.firstName} " +
                            "with id# ${response.await().data.id}"
                )
            } catch (e: Exception) {
                Log.d(TAG, "Exception occurred during Add Contact coroutine: ${e.message}")
            }
        }
    }

    suspend fun putContact(contact: Contact): Contact { // TODO: should probably return a Contact for updating currentContact in viewmodel
         /**
         * Suspend function to PUT a revised Contact to the webserver.
         * Catches the response contact and updates database
         */
        return withContext(Dispatchers.IO) {
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
                        database.contactDao.insertContact(databaseContactFromEntryAdapter(this.data))
                        Log.d(TAG,
                            "Inserted into database: " +
                                    "${this.data.attributes.firstName} " +
                                    "with id# ${this.data.id}"
                        )
                        return@withContext contactFromEntryAdapter(response.await().body()!!.data)
                }
                } else {
                    //Log.d(TAG,"Error response from PUT request: ${response.await().errorBody()}")
                    return@withContext contact
                }
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

    // TODO: query database for contacts with an overdue/upcoming status


    // Sorting Method
    private val byFirstName = Comparator.comparing<Contact, String> { contact: Contact ->
        contact.firstName.lowercase()
    }
}