package com.example.kit.ui.contactdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactDetailViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Contact Detail fragment"
    }
    val text: LiveData<String> = _text

    // TODO: Implement the ViewModel
}