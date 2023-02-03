package com.example.kit.ui.addcontact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddContactViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Add Contact fragment"// R.string.title_editcontact.toString()
    }
    val text: LiveData<String> = _text

// TODO: Implement the ViewModel
}