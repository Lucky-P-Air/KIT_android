package com.example.kit.data

import androidx.room.*
import com.example.kit.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts_table ORDER BY LOWER(first_name) ASC") // Alphabetical by first name
    fun observeAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts_table ORDER BY LOWER(first_name) ASC") // Alphabetical by first name
    fun getAllContacts(): List<Contact>


    @Query("SELECT * FROM contacts_table WHERE status IS NOT '' ORDER BY status, last_contacted ASC")
    fun observeStatusContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts_table WHERE status IS NOT '' ORDER BY status, last_contacted ASC")
    fun getStatusContacts(): List<Contact>


    @Query("SELECT * FROM contacts_table WHERE id IS :id")
    fun observeContact(id: String): Flow<Contact>

    @Query("SELECT * FROM contacts_table WHERE id IS :id")
    fun getContact(id: String): Contact


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(contacts: List<Contact>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateContact(contact: Contact)

    @Delete
    fun deleteContact(contact: Contact)
}