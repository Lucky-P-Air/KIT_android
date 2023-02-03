package com.example.kit.ui.contactlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Contact List fragment"// R.string.title_contactlist.toString()
    }
    val text: LiveData<String> = _text

// TODO: Implement the ViewModel
}