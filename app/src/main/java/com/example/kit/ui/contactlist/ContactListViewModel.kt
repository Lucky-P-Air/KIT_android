package com.example.kit.ui.contactlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kit.data.ContactSource
import com.example.kit.model.Contact

class ContactListViewModel : ViewModel() {

    // TODO: Delete these (3) temporary variables used for development
    private val _text = MutableLiveData<String>("This is the alternate Detail fragment")
    val text: LiveData<String> = _text
    // Temporary object for connecting to ContactSource() until a database is used
    // This particular instance might get lost if ViewModel
    private var dataSource = ContactSource()

    // list of Contacts data
    private lateinit var _list: MutableLiveData<MutableList<Contact>>
    val list: LiveData<MutableList<Contact>> get() = _list

    // Specific contact detail properties

    private lateinit var _currentContact: MutableLiveData<Contact>
    val currentContact: LiveData<Contact> get() = _currentContact


    init {
        getContactList()

    }

    // Load or refresh full list of contacts, sorted by name
    private fun getContactList() {
        // Sorting Method
        val byFirstName  = Comparator.comparing<Contact, String> {
                contact: Contact -> contact.firstName.lowercase()
        }
        // Last name is nullable so don't use it for sorting at this time

        _list = MutableLiveData(dataSource.sourceContactList.sortedWith(byFirstName).toMutableList()
        )
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
        dataSource.sourceContactList
            .add(
                Contact(firstName, lastName, email, phoneNumber, intervalTime, intervalUnit)
            )

        // Update ._list with appended contact list, sorted by name
        getContactList()
    }


    // fun getContactDetail(position: Int) : {    }


    override fun onCleared() {
        super.onCleared()
        Log.d("ContactListViewModel", "Instance of ContactViewModel has been cleared. Any updates to contact list have been lost")
    }
    /* Not currently needed
    private fun sortContactList(contactList: List<Contact>) : List<Contact> {
        // Function for sorting names
        val byFirstName  = Comparator.comparing<Contact, String> {
                contact: Contact -> contact.firstName.lowercase()
        }
        // Last name is nullable so don't use it for sorting at this time
        return contactList.sortedWith(byFirstName)
    }
    */
}