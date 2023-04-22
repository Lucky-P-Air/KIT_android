package com.example.kit.data

import androidx.room.*
import com.example.kit.model.DatabaseContact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts_table ORDER BY LOWER(first_name) ASC") // Alphabetical by first name
    fun getAllContacts(): Flow<List<DatabaseContact>>

    @Query("SELECT * FROM contacts_table WHERE status IS NOT '' ORDER BY status, last_contacted ASC")
    fun getStatusContacts(): Flow<List<DatabaseContact>>

    @Query("SELECT * FROM contacts_table WHERE id IS :id")
    fun getContact(id: String): Flow<DatabaseContact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contact: DatabaseContact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(contacts: List<DatabaseContact>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateContact(contact: DatabaseContact)

    @Delete
    fun deleteContact(contact: DatabaseContact)
}