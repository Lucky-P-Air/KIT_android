package com.example.kit.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission
import com.example.kit.model.asContacts
import com.example.kit.model.contactFromEntryAdapter
import com.example.kit.network.ContactRequest
import com.example.kit.network.contactPushFromContactAdapter
import com.example.kit.network.contactPushFromContactSubmissionAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

private const val TAG = "RemoteDataSource"

class RemoteDataSource internal constructor(
    private val apiService: ContactApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataSourceInterface {

    private val observableContacts = MutableLiveData<List<Contact>>()
    //private val observableContact = MutableLiveData<Contact>()

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
        try {
            //Serialize new contact information into JSON-ready object
            val contactPost =
                ContactRequest(contactPushFromContactSubmissionAdapter(contactSubmission))
            withContext(ioDispatcher) {
                //val response = async {
                apiService.postNewContact(
                    Secrets().headers,
                    contactPost
                    )
                //}
                //observableContact(contactFromEntryAdapter(response.await().data).id)
            }
            Log.d(TAG, "ContactRequest: $contactPost")
        } catch (e: Exception) {
            Log.d(TAG, "Exception occurred during Add Contact coroutine: ${e.message}")
        }
    }

    override suspend fun deleteContact(contact: Contact) {
        try {
            withContext(ioDispatcher) {
                apiService.deleteContact(
                    contact.id,
                    Secrets().headers
                )
            }
            Log.d(TAG, "Deleting contact from API successful")
        } catch (e: Exception) {
            Log.d(TAG, "Exception while deleting contact from network")
            throw e
        }
    }

    override suspend fun getContacts(): List<Contact> {
        return observeContacts().value!!
    }

    override suspend fun getContactDetail(contactID: String): Contact {
        try {
            return withContext(ioDispatcher) {
                val singleContactResponse = apiService.getSingleContact(
                    contactID,
                    Secrets().headers
                )
                refreshContacts()
                observeContact(singleContactResponse.data.id!!)
                return@withContext contactFromEntryAdapter(singleContactResponse.data)
                //TODO: may not have to return the Contact if the observableContact is observed?
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception while getting contact detail update from network")
            throw e
        }
    }

    override suspend fun refreshContacts() {
        try {
            withContext(ioDispatcher) {
                val response = apiService.getContacts(Secrets().headers)
                observableContacts.postValue(response.asContacts())
            }
            Log.d(TAG,"Get request success; retrieved ${observableContacts.value!!.size} entries.")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveContact(contact: Contact): Contact {
        try {
            //Serialize new contact information into JSON-ready object
            val contactPut = ContactRequest(contactPushFromContactAdapter(contact))
            Log.d(TAG, "ContentRequest(UpdateContact): $contactPut")
            return withContext(ioDispatcher) {
                val response = async {
                    apiService.updateContact(
                        contact.id,
                        Secrets().headers,
                        contactPut
                    )
                }
                if (response.await().isSuccessful) {
                    return@withContext contactFromEntryAdapter(response.await().body()!!.data)
                } else {
                    val message = response.await().message()
                    //val body = response.await().errorBody() // Extract additional http error msg
                    Log.d(TAG, "Network Error: $message")
                    throw Exception(message)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }
}