package com.example.kit.ui.contactlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit.data.ContactApi
import com.example.kit.data.ContactSource
import com.example.kit.data.Secrets
import com.example.kit.model.Contact
import com.example.kit.model.ContactEntry
import kotlinx.coroutines.launch

class ContactListViewModel : ViewModel() {

    // TODO: Delete these (2) temporary variables used for development
    val INTERVAL_UNITS = listOf<String>("days", "weeks", "months", "years")
    // Temporary object for connecting to ContactSource() until a database is used
    // This particular instance might get lost if ViewModel
    private var dataSource = ContactSource()

    // list of ContactEntry's from API response
    private var _responseList = MutableLiveData<List<ContactEntry>>()
    val responseList: LiveData<List<ContactEntry>> = _responseList

    // list of Contacts data
    private var _list = MutableLiveData<MutableList<Contact>>()
    val list: LiveData<MutableList<Contact>> = _list

    // TODO Delete positions because they're not really used after clickListener binding implementation.
    // Need to clean up 'position' references in EditContact fragment
    private lateinit var _position: MutableLiveData<Int> // position index within sorted list
    val position : LiveData<Int> get() = _position
    // Specific contact detail properties
    private var _currentContact = MutableLiveData<Contact>()
    val currentContact: LiveData<Contact> = _currentContact


    init {
        //getContactList() // Moved initial GET to ContactListFragment
    }

    /* Adds a contact to the (temporary) ContactSource.sourceContactList
    that will eventually be replaced by a method connected to a database
    */
    fun addContact(
        firstName: String,
        lastName: String?,
        email: String?,
        phoneNumber: String?,
        intervalTime: Int,
        intervalUnit: String) {
        Log.d("ContactListViewModel", "addContact method called")
        dataSource.sourceContactList
            .add(
                Contact(firstName, lastName, email, phoneNumber, intervalTime, intervalUnit)
            )

        // Update ._list with appended contact list, sorted by name
        getContactList()
    }

    //  fun deleteContact() disabled during setup of connectivity
    fun deleteContact() {
        /** Deletes _currentContact.value from the ViewModel's _list of contacts
         * Function is currently implemented to only be called from ContactDetail page
         * and only affects the ViewModels datasource. */

        dataSource.sourceContactList.remove(currentContact.value)
        Log.d("ContactListViewModel", "Contact deleted from dataSource. List not refreshed yet")
        getContactList()
        Log.d("ContactListViewModel", "Contact List Refreshed after contact deletion")
    }

    // Load or refresh full list of contacts, sorted by name
    /*private fun getContactList() {
        // Sorting Method
        val byFirstName  = Comparator.comparing<Contact, String> {
                contact: Contact -> contact.firstName.lowercase()
        }
        // Last name is nullable so don't use it for sorting at this time

        _list = MutableLiveData(dataSource.sourceContactList.sortedWith(byFirstName).toMutableList()
        )
    }*/ // local hard-coded data source
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
                    "ContactSource",
                    "Get request successful, retrieved ${responseList.value?.size} entries"
                )
                // Transform nested ContactEntry structure to flat Contact class
                val listContacts = responseList.value?.map() {
                    Contact(
                        it.attributes.firstName,
                        it.attributes.lastName,
                        it.attributes.email,
                        it.attributes.phoneNumber,
                        it.attributes.intervalNumber,
                        it.attributes.intervalUnit
                    )
                }?.toMutableList()
                Log.d("ContactSource", "Transform produced listContacts of size ${listContacts?.size} entries")
                Log.d(
                    "ContactSource",
                    "First entry is ${listContacts?.get(0)?.firstName}"
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

    // Removed for ListAdapter update & binding
    /*
    fun setCurrentContact(position: Int) {
        _position = MutableLiveData(position)
        _currentContact = MutableLiveData(list.value?.get(position))
    }*/

    // fun getContactDetail(position: Int) : {    }

    //  fun updateContact() disabled during setup of connectivity
    fun updateContact(
        firstName: String,
        lastName: String?,
        email: String?,
        phoneNumber: String?,
        intervalTime: Int,
        intervalUnit: String) {
        Log.d("ContactListViewModel", "updateContact method called")

        // find position index within UNSORTED data source list
        val sourcePosition = dataSource.sourceContactList.indexOf(currentContact.value)
        dataSource.sourceContactList[sourcePosition] =
            Contact(firstName, lastName, email, phoneNumber, intervalTime, intervalUnit)

        // Update ._list with appended contact list, sorted by name
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