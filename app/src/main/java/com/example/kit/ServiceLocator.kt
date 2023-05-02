package com.example.kit

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.example.kit.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object ServiceLocator {

    private var database: ContactsDatabase? = null
    @Volatile
    var contactRepository: RepositoryInterface? = null
        @VisibleForTesting set
    private val lock = Any()

    fun provideContactRepository(context: Context): RepositoryInterface {
        synchronized(this) {
            return contactRepository ?: createRepositoryInterface(context)
        }
    }

    private fun createRepositoryInterface(context: Context): RepositoryInterface {
        val newRepo = ContactRepository(
            createContactsLocalDataSource(context),
            RemoteDataSource(ContactApi.retrofitService),
            Dispatchers.IO
        )
        contactRepository = newRepo
        return newRepo
    }

    private fun createContactsLocalDataSource(context: Context): DataSourceInterface {
        val database = database ?: createDatabase(context)
        return LocalDataSource(database.contactDao, Dispatchers.IO)
    }

    private fun createDatabase(context: Context): ContactsDatabase {
        val result: ContactsDatabase by lazy { getDatabase(context)}
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                //TODO: RemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            contactRepository = null
        }
    }
}