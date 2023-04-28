package com.example.kit.ui.contactlist

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import com.example.kit.data.ContactRepository
import com.example.kit.data.getDatabase
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission
import com.example.kit.model.DatabaseContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private const val TAG = "ContactListViewModel"

class ContactListViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepository(getDatabase(application))
    lateinit var currentId: String
    private var _list = contactRepository.allContacts
    val list: LiveData<List<Contact>> get() = _list

    init {
        //getContactList() // Moved initial GET to ContactListFragment
    }

    fun addContact(
        firstName: String,
        lastName: String?,
        phoneNumber: String?,
        email: String?,
        intervalTime: Int,
        intervalUnit: String
    ) {
        val newContact = ContactSubmission(
            firstName,
            lastName,
            phoneNumber,
            email,
            true,
            // null,
            intervalUnit,
            intervalTime
        )

        //TODO Error catching, logging, and communication from server responses
        //val submittalSuccess = viewModelScope.async {
        try {
            //viewModelScope.launch {
            runBlocking {
                contactRepository.postContact(newContact)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error occurred trying to add contact. ${e.message}")
            throw e
        }
        getContactList()
    }

    fun deleteContact(dbContact: DatabaseContact) {
        /** Deletes contact (default to databaseContact.value) from the server & database
         * Function is currently implemented to only be called from ContactDetail page
         */
        Log.d(TAG, "Deleting ${dbContact.id}")
        //viewModelScope.launch {
        runBlocking {
            try {
                contactRepository.deleteContact(dbContact)
                Log.d(TAG, "Contact List Refreshed after contact deletion")
            } catch (e: Exception) {throw e}
        }
        currentId = "0"
    }

    // GET updated data from API & insert into database
    private fun fetchContactDetail(contactID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.fetchContactDetail(contactID)
        }
    }

    fun getContactDetail(contactID: String): LiveData<Contact> {
        fetchContactDetail(contactID)
        return contactRepository.getContactDetail(contactID)
    }

    fun getDatabaseContactDetail(contactID: String): LiveData<DatabaseContact> {
        fetchContactDetail(contactID)
        return contactRepository.getDatabaseContact(contactID)
    }

    // Load / refresh full list of contacts, sorted by name
    fun getContactList() {
        viewModelScope.launch { contactRepository.refreshContacts() }
    }

    fun markContacted(contact: Contact) {
        val timeNow = Instant.now().atZone(ZoneId.of("UTC"))
            .toLocalDateTime()
        Log.d(TAG, "$timeNow for ${contact.firstName}")
        sendUpdate(contact.copy(lastContacted = timeNow))
    }

    fun onContactClicked(contact: Contact) {
        Log.d(TAG, "Contact ${contact.id} clicked in RecyclerView")
        setContactId(contact.id)
        fetchContactDetail(contact.id)
    }

    private fun sendUpdate(contact: Contact) {
        //viewModelScope.launch {
        runBlocking {
            try {
                contactRepository.putContact(contact)
            } catch (e: Exception) {throw e}
        }
    }

    private fun setContactId(id: String) {
        currentId = id
    }

    fun updateContact(
        id: String,
        firstName: String,
        lastName: String?,
        phoneNumber: String?,
        email: String?,
        intervalTime: Int,
        intervalUnit: String,
        remindersEnabled: Boolean,
        lastContacted: LocalDateTime?,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime,
        status: String,
    ) {
        Log.d("ContactListViewModel", "updateContact method called")

        val contactRevised = Contact(
            id,
            firstName,
            lastName ?: "",
            phoneNumber ?: "",
            email ?: "",
            intervalTime,
            intervalUnit,
            remindersEnabled,
            lastContacted,
            createdAt,
            updatedAt,
            status
        )
        sendUpdate(contactRevised)
        getContactList()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "Instance of ContactViewModel has been cleared")
    }

    fun errorEmail(emailValue: String): Boolean {
        /**
         * Return 'true' if there's an error in the email format.
         * Format-pattern-matching via Patterns.EMAIL_ADDRESS
         */
        if (emailValue.isEmpty()) return false
        // Return true if emailValue does not match EMAIL_ADDRESS pattern
        return !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()
    }

    fun errorPhoneNumber(phoneValue: String): Boolean {
        /**
         * Return 'true' if there's an error in the phone number format.
         *
         * Django backend API requires phone numbers to have:
         *   +1 Country Code (only one currently compatible)
         *   10 numerical digits
         */
        fun isValidLength(phone: String, length: Int = 10): Boolean {
            return phone.length == length
        }

        return if (isValidLength(phoneValue)) {
            // Reject any with Country Codes operator +
            when (phoneValue.first()) {
                '+' -> return true
            }
            Log.d(TAG,"Valid 10-digit phone number ($phoneValue) provided. Adding country code +1")
            false
        } else {
            true
        }
    }
}

/**
 * Factory for constructing ContactListViewModel with parameter
 */
class ContactListViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactListViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}