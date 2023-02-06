package com.example.kit.ui.contactlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kit.data.ContactSource
import com.example.kit.model.Contact

class ContactListViewModel : ViewModel() {

    // list of Contacts data
    private lateinit var _list: MutableLiveData<List<Contact>>
    val list: LiveData<List<Contact>> get() = _list

    init {
        getContactList()
    }

    // Load or refresh full list of contacts, sorted by name
    fun getContactList() {
        // Function for sorting names
        val byFirstName  = Comparator.comparing<Contact, String> {
                contact: Contact -> contact.firstName.lowercase()
        }
        // Last name is nullable so don't use it for sorting at this time

        _list = MutableLiveData(ContactSource().loadContacts().sortedWith(byFirstName))
    }

    // fun addContact() {
        /* Method of adding a contact might require turning _list into a MutableList?
        Depends what the state of the ContactSource class/database is, and whether there's
        another way to append it during the progression of this ViewModel's development
        }
         */

    // fun getContactDetail(position: Int) {    }
}