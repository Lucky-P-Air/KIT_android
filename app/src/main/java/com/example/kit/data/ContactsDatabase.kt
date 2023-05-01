package com.example.kit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kit.model.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class ContactsDatabase: RoomDatabase() {
    abstract val contactDao: ContactDao
}

private lateinit var INSTANCE: ContactsDatabase

fun getDatabase(context: Context): ContactsDatabase {
    // Singleton pattern for building and providing a Room database
    synchronized(ContactsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            ContactsDatabase::class.java,
            "contacts_table")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}