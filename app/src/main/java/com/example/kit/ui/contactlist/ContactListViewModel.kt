package com.example.kit.ui.contactlist

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.kit.data.ContactApi
import com.example.kit.data.ContactRepository
import com.example.kit.data.Secrets
import com.example.kit.data.getDatabase
import com.example.kit.model.Contact
import com.example.kit.model.ContactSubmission
import com.example.kit.model.contactFromEntryAdapter
import com.example.kit.utils.formatLocalDateTimes
import com.example.kit.utils.getNextContactLocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private const val TAG = "ContactListViewModel"

class ContactListViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepository(getDatabase(application))

    // list of Contacts data
    //private var _list = MutableLiveData<List<Contact>>() // Replaced when repository pattern was added
    private var _list = contactRepository.allContacts
    val list: LiveData<List<Contact>> get() = _list

    // Specific contact detail properties
    private var _currentContact = MutableLiveData<Contact?>()
    val currentContact: LiveData<Contact?> get() = _currentContact

    // LiveData for specific attributes of the currentContact.
    //    Includes pre-processing of strings/values for displaying in UI
    val liveFirstName = Transformations.map(_currentContact) { it!!.firstName }
    val liveLastName = Transformations.map(_currentContact) { it!!.lastName }
    val livePhoneNumber = Transformations.map(_currentContact) { it!!.phoneNumber }
    val liveEmail = Transformations.map(_currentContact) { it!!.email }
    val liveRemindersEnabled = Transformations.map(_currentContact) { it!!.remindersEnabled }
    val liveIntervalTime = Transformations.map(_currentContact) { it!!.intervalTime }

    //val liveIntervalUnit = Transformations.map(_currentContact) { it!!.intervalUnit } // unneeded
    val liveLastContacted = Transformations.map(_currentContact) {
        it!!.lastContacted?.let { localDateTime -> formatLocalDateTimes(localDateTime) } ?: "Never"
    }
    val liveNextReminder = Transformations.map(_currentContact) {
        formatLocalDateTimes(getNextContactLocalDateTime(it!!))
    }
    val liveStatus = Transformations.map(_currentContact) {
        it!!.status.let { statusString ->
            statusString.replaceFirstChar { char -> char.uppercase() }
        }
    }

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
        viewModelScope.launch {
            contactRepository.postContact(newContact)
        }
        // Update ._list with appended contact list, sorted by name
        getContactList()
    }

    fun deleteContact(contactID: String = currentContact.value!!.id) {
        /** Deletes contact (default to currentContact.value.id) from the server
         * Function is currently implemented to only be called from ContactDetail page
         */
        viewModelScope.launch {
            try {
                Log.d(TAG, "Delete Contact coroutine launched for $contactID")
                ContactApi.retrofitService.deleteContact(
                    contactID,
                    Secrets().headers
                )
                Log.d(TAG, "Delete Contact coroutine SUCCESS")
                _currentContact.postValue(null)
            } catch (e: Exception) {
                Log.d(TAG, "Exception occurred during Delete Contact coroutine: ${e.message}")
            }
        }
        getContactList()
        Log.d(TAG, "Contact List Refreshed after contact deletion")
    }

    // Load or refresh full list of contacts, sorted by name
    fun getContactList() {
        viewModelScope.launch { contactRepository.refreshContacts() }
    }

    fun onContactClicked(contact: Contact) {
        Log.d(TAG, "Contact ${contact.id} clicked in RecyclerView")
        _currentContact.postValue(contact) // Locally cached value from List GET until replace by next GET request
        getContactDetail(contact.id)
    }

    fun getContactDetail(contactID: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val singleContactResponse = ContactApi.retrofitService.getSingleContact(
                    contactID,
                    Secrets().headers
                )
                Log.d(TAG, "Contact ${singleContactResponse.data} retrieved from API")
                withContext(Dispatchers.Main) {
                    _currentContact.value = contactFromEntryAdapter(singleContactResponse.data)
                    Log.d(TAG, "_currentContact.value has been updated")
                }
            } catch (e: Exception) {
                Log.d(TAG, "Exception occurred during getContactDetail operation: ${e.message}")
            }
        }
    }

    fun markContacted() {
        val timeNow = Instant.now().atZone(ZoneId.of("UTC"))
            .toLocalDateTime()
        viewModelScope.launch {
            with(currentContact.value!!) {
                val responseContact = contactRepository.putContact(this.copy(lastContacted = timeNow))
                _currentContact.postValue(responseContact)
            }
        }
    }

    fun updateContact(
        firstName: String,
        lastName: String?,
        phoneNumber: String?,
        email: String?,
        intervalTime: Int,
        intervalUnit: String,
        remindersEnabled: Boolean,
        lastContacted: LocalDateTime? = currentContact.value!!.lastContacted
    ) {
        Log.d("ContactListViewModel", "updateContact method called")

        val contactRevised = Contact(
            currentContact.value!!.id,
            firstName,
            lastName ?: "",
            phoneNumber ?: "",
            email ?: "",
            intervalTime,
            intervalUnit,
            remindersEnabled,
            lastContacted,
            currentContact.value!!.createdAt,
            currentContact.value!!.updatedAt,
            currentContact.value!!.status
        )
        viewModelScope.launch {
            val responseContact = contactRepository.putContact(contactRevised)
            _currentContact.postValue(responseContact)
        }
        getContactList()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "Instance of ContactViewModel has been cleared")
    }

    /**
     * Factory for constructing ContactListViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ContactListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ContactListViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}