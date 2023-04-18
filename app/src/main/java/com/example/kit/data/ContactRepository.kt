package com.example.kit.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.example.kit.model.Contact
import com.example.kit.model.DatabaseContact
import com.example.kit.model.asContacts
import com.example.kit.model.asDatabaseContacts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "ContactRepository"
class ContactRepository(private val database: ContactsDatabase) {

    val dbContacts: LiveData<List<DatabaseContact>> = database.contactDao.getAllContacts().asLiveData()
    // Convert list of DbContacts to Contacts (Domain Model)
    val allContacts: LiveData<List<Contact>> = Transformations.map(dbContacts) {
            it.asContacts()
                .sortedWith(byFirstName)
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