package com.example.kit.ui.contactlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kit.data.ContactSource
import com.example.kit.model.Contact

class ContactListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Contact List fragment"// R.string.title_contactlist.toString()
    }
    val text: LiveData<String> = _text

    // Retrieve list of Contacts data
    private lateinit var _list: MutableLiveData<MutableList<Contact>>
    val list: LiveData<MutableList<Contact>> get() = _list

    init {
        getContactList()
    }

    // Load or refresh full list of contacts
    fun getContactList() {
        _list = MutableLiveData(ContactSource().loadContacts())
    }

    // fun addContact() {}

    // fun getContactDetail() {}
}