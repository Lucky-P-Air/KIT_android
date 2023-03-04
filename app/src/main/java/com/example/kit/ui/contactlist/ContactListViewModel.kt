package com.example.kit.ui.contactlist

import android.util.Log
import androidx.lifecycle.*
import com.example.kit.data.ContactApi
import com.example.kit.data.Secrets
import com.example.kit.model.Contact
import com.example.kit.model.ContactEntry
import com.example.kit.model.ContactSubmission
import com.example.kit.model.contactFromEntryAdapter
import com.example.kit.network.ContactRequest
import com.example.kit.network.contactPushFromContactAdapter
import com.example.kit.network.contactPushFromContactSubmissionAdapter
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val TAG = "ContactListViewModel"
class ContactListViewModel : ViewModel() {

    // list of ContactEntry's from API response
    private var _responseList = MutableLiveData<List<ContactEntry>>()
    val responseList: LiveData<List<ContactEntry>> = _responseList

    // list of Contacts data
    private var _list = MutableLiveData<MutableList<Contact>>() //TODO Doesn't need to be MutableList
    val list: LiveData<MutableList<Contact>> get() = _list

    // Specific contact detail properties
    private var _currentContact = MutableLiveData<Contact?>()
    val currentContact: LiveData<Contact?> get() = _currentContact

    // LiveData for remindersEnabled boolean
    val reminderLiveData = Transformations.map(_currentContact) { it!!.remindersEnabled }

    init {
        //getContactList() // Moved initial GET to ContactListFragment
    }

    fun addContact(
        firstName: String,
        lastName: String?,
        phoneNumber: String?,
        email: String?,
        intervalTime: Int,
        intervalUnit: String) {
        Log.d("ContactListViewModel", "addContact method called on ${firstName} ${lastName}")
        val newContact = ContactSubmission(
            firstName,
            lastName,
            phoneNumber,
            email,
            true,
            // null,
            intervalUnit,
            intervalTime)


        //TODO Error catching, logging, and communication from server responses
        //TODO update navigation direction after successful POST to contact detail
        //val submittalSuccess = viewModelScope.async {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Add Contact coroutine launched for $newContact")
                //Serialize new contact information into JSON-ready object
                val contactPost = ContactRequest(contactPushFromContactSubmissionAdapter(newContact))
                Log.d(TAG, "ContactRequest: $contactPost")
                val response = ContactApi.retrofitService.postNewContact(
                    Secrets().headers,
                    contactPost)
                Log.d(TAG, "Add Contact coroutine SUCCESS for ${response.data.attributes.firstName} with id# ${response.data.id}")
                //TODO assign response contact to ViewModel's _currentcontact.value
                _currentContact.postValue(contactFromEntryAdapter(response.data))
                //return@async true // Assumed to be true
            } catch (e: Exception) {
                Log.d(TAG, "Exception occurred during Add Contact coroutine: ${e.message}")
                //return@async false
            }
        }

        // Update ._list with appended contact list, sorted by name
        getContactList()
    }

    //  fun deleteContact() disabled during setup of connectivity
    fun deleteContact() {
        /** Deletes currentContact.value.id from the server
         * Function is currently implemented to only be called from ContactDetail page
         */
        viewModelScope.launch {
            try {
                Log.d(TAG, "Delete Contact coroutine launched for ${currentContact.value!!.id}")
                ContactApi.retrofitService.deleteContact(
                    currentContact.value!!.id,
                    Secrets().headers)
                Log.d(TAG, "Delete Contact coroutine SUCCESS")
                _currentContact.postValue(null)
            } catch (e: Exception) {
                Log.d(TAG, "Exception occurred during Add Contact coroutine: ${e.message}")
            }
        }
        getContactList()
        Log.d(TAG, "Contact List Refreshed after contact deletion")
    }

    // Load or refresh full list of contacts, sorted by name
    fun getContactList() {
        // Sorting Method
        val byFirstName = Comparator.comparing<Contact, String> { contact: Contact ->
            contact.firstName.lowercase()
        }
        viewModelScope.launch {
            try {
                val response = ContactApi.retrofitService.getContacts(Secrets().headers)
                _responseList.value = response.data
                Log.d(
                    "ContactListViewModel",
                    "Get request successful, retrieved ${responseList.value?.size} entries."
                )
                Log.d(
                    "ContactListViewModel",
                    "First Contact Entry: ${responseList.value!![0]}"
                )
                // Transform nested ContactEntry structure to flat Contact class
                val listContacts = responseList.value?.map() {
                    contactFromEntryAdapter(it)
                }?.toMutableList()
                Log.d("ContactSource", "Transform produced listContacts of size ${listContacts?.size} entries")
                Log.d(
                    "ContactSource",
                    "First entry is ${listContacts?.get(0)?.firstName} ${listContacts?.get(0)?.lastName} "
                )

                // Last name is nullable so don't use it for sorting at this time
                _list.value = listContacts?.sortedWith(byFirstName)?.toMutableList()
            } catch (e: java.lang.Exception) {
                Log.d("ContactSource", "Exception during getContactList coroutine: ${e.toString()}")
                _responseList.value = listOf()
                _list.value = mutableListOf()
            }
        }
    }

    fun onContactClicked(contact: Contact) {
        Log.d(TAG,"Contact ${contact.id} clicked in RecyclerView")
        _currentContact.postValue(contact) // Locally cached value from List GET until replace by next GET request
        //getContactDetail(contact.id)
    }

    fun getContactDetail(contactID: String) {
        /*
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val singleContactResponse = ContactApi.retrofitService.getSingleContact(
                    contactID,
                    Secrets().headers
                )
                Log.d(TAG,"Contact ${singleContactResponse.data} retrieved from API")
                withContext(Dispatchers.Main) {
                    _currentContact.value = contactFromEntryAdapter(singleContactResponse.data)
                    Log.d(TAG,"_currentContact.value has been updated")
                }
            } catch (e: Exception) {
                Log.d(TAG,"Exception occurred during getContactDetail operation: ${e.message}")
            }
        }
        */
    }

    // TODO debug why updating values to null (like phone/email) dont push to server
    fun updateContact(
        firstName: String,
        lastName: String?,
        phoneNumber: String?,
        email: String?,
        intervalTime: Int,
        intervalUnit: String,
        remindersEnabled: Boolean) {
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
            currentContact.value!!.lastContacted,
            currentContact.value!!.createdAt,
            currentContact.value!!.updatedAt,
            currentContact.value!!.status
        )
        viewModelScope.launch {
            try {
                Log.d(
                    "ContactListViewModel",
                    "Initiating EditContact PUT request for $contactRevised"
                )
                //Serialize new contact information into JSON-ready object
                val contactPut = ContactRequest(contactPushFromContactAdapter(contactRevised))
                Log.d("ContactListViewModel", "ContentRequest(UpdateContact): $contactPut")
                val response = async { ContactApi.retrofitService.updateContact(
                    contactRevised.id,
                    Secrets().headers,
                    contactPut
                )}
                Log.d("ContactListViewModel", "PUT request successful? ${response.await().isSuccessful}")
                Log.d("ContactListViewModel", "Response message from PUT request: ${response.await().message()}")
                Log.d("ContactListViewModel", "Response from PUT request: ${response.await().body()!!.data}")
                //TODO assign response contact to ViewModel's _currentcontact.value
                _currentContact.postValue(contactFromEntryAdapter(response.await().body()!!.data))
            } catch (e: Exception) {
                Log.d(
                    "ContactListViewModel",
                    "Exception occurred during updateContact operation: ${e.message}"
                )
                //return@async false
            }
        }
        getContactDetail(currentContact.value!!.id)
        getContactList()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ContactListViewModel", "Instance of ContactViewModel has been cleared. Any updates to contact list have been lost")
    }
    // fun sortContactList Not currently needed
    private fun sortContactList(contactList: List<Contact>) : List<Contact> {
        // Function for sorting names
        val byFirstName  = Comparator.comparing<Contact, String> {
                contact: Contact -> contact.firstName.lowercase()
        }
        // Last name is nullable so don't use it for sorting at this time
        return contactList.sortedWith(byFirstName)
    }

    fun toggleReminders() {
        /**
         *  Function to toggle reminders on/off for a particular contact from contact detail page
         */
    }

    }