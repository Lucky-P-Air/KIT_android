package com.example.kit.ui.contactlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit.data.ContactApi
import com.example.kit.data.Secrets
import com.example.kit.model.*
import kotlinx.coroutines.launch

class ContactListViewModel : ViewModel() {

    // list of ContactEntry's from API response
    private var _responseList = MutableLiveData<List<ContactEntry>>()
    val responseList: LiveData<List<ContactEntry>> = _responseList

    // list of Contacts data
    private var _list = MutableLiveData<MutableList<Contact>>() //TODO Doesn't need to be MutableList
    val list: LiveData<MutableList<Contact>> = _list

    // Specific contact detail properties
    private var _currentContact = MutableLiveData<Contact>()
    val currentContact: LiveData<Contact> = _currentContact


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
                Log.d("ContactListViewModel", "Add Contact coroutine launced for $newContact")
                //Serialize new contact information into JSON-ready object
                val contactPost = ContactRequest(entryFromContactSubmissionAdapter(newContact))
                Log.d("ContactListViewModel", "ContentRequest: $contactPost")
                val response = ContactApi.retrofitService.postNewContact(
                    Secrets().headers,
                    contactPost)
                Log.d("ContactListViewModel", "Add Contact coroutine SUCCESS for ${response.data.attributes.firstName} with id# ${response.data.id}")
                //TODO assign response contact to ViewModel's _currentcontact.value
                _currentContact.value = contactFromEntryAdapter(response.data)
                //return@async true // Assumed to be true
            } catch (e: Exception) {
                Log.d("ContactListViewModel", "Exception occurred during Add Contact coroutine: ${e.message}")
                //return@async false
            }
        }

        // Commented out during Contact model refactor
        //dataSource.sourceContactList
            //.add(Contact(firstName, lastName, email, phoneNumber, intervalTime, intervalUnit))

        // Update ._list with appended contact list, sorted by name
        getContactList()
    }

    //  fun deleteContact() disabled during setup of connectivity
    fun deleteContact() {
        /** Deletes _currentContact.value from the ViewModel's _list of contacts
         * Function is currently implemented to only be called from ContactDetail page
         * and only affects the ViewModels datasource. */

        // Commented out during Contact model refactor
        //dataSource.sourceContactList.remove(currentContact.value)
        Log.d("ContactListViewModel", "Contact deleted from dataSource. List not refreshed yet")
        getContactList()
        Log.d("ContactListViewModel", "Contact List Refreshed after contact deletion")
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
        _currentContact.value = contact
    }

    // fun getContactDetail(position: Int) : {    }

    //  fun updateContact() disabled during setup of connectivity
    fun updateContact(
        firstName: String,
        lastName: String?,
        phoneNumber: String?,
        email: String?,
        intervalTime: Int,
        intervalUnit: String) {
        Log.d("ContactListViewModel", "updateContact method called")

        val contactSubmission = ContactSubmission(
            firstName,
            lastName,
            phoneNumber,
            email,
            true,
            intervalUnit,
            intervalTime)
        Log.d("ContactListViewModel", "Updating ${contactSubmission}")

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

    }