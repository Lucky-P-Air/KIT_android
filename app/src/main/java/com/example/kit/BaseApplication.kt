package com.example.kit

import android.app.Application
import com.example.kit.data.ContactsDatabase
import com.example.kit.data.getDatabase

class BaseApplication: Application() {
    val database: ContactsDatabase by lazy {getDatabase(this)}
}