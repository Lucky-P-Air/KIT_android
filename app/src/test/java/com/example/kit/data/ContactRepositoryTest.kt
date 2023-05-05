package com.example.kit.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kit.model.Contact
import com.example.kit.utils.getTimeEpochString
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
internal class ContactRepositoryTest {

    // Establish starter data to populate local & remote data sources
    private val epochTime = getTimeEpochString()
    private val fakeTime = epochTime
    private val contact1 = Contact("1", "Michael", "Scott", null, null, 2, "days", true, fakeTime, fakeTime, fakeTime, "overdue")
    private val contact2 = Contact("2", "Pam", "Beasley", null, null, 2, "days", true, fakeTime, fakeTime, fakeTime, "upcoming")
    private val contact3 = Contact("3", "Princess", "Zelda", null, null, 2, "days", true, fakeTime, fakeTime, fakeTime, "")
    private val remoteContacts = HashMap<String, Contact>()

    // Faked dependencies
    private lateinit var remoteDataSource: FakeRemoteDataSource
    private lateinit var localDataSource: FakeLocalDataSource
    // Class under test
    private lateinit var contactRepository: ContactRepository
    @Before
    fun createRepository() {
        remoteContacts.putAll(listOf(
            contact1.id to contact1,
            contact2.id to contact2)
        )
        remoteDataSource = FakeRemoteDataSource(remoteContacts)
        localDataSource = FakeLocalDataSource()
        contactRepository = ContactRepository(
            localDataSource,
            remoteDataSource,
            Dispatchers.Unconfined
        )
    }

    @Test
    fun deleteContact_nominal_throwsException() {
        // GIVEN a remote data source with 2 records
        //       and a local source with 1 record (common to local & remote sources)
        val contact = contact2
        localDataSource.fakeDatabase[contact.id] = contact
        // WHEN the repository deletes a contact
        runBlocking { contactRepository.deleteContact(contact) }
        // THEN both data sources should throw exception
        assertNull(localDataSource.fakeDatabase[contact.id])
        assertNull(remoteDataSource.fakeDatabase[contact.id])
    }

    @Test
    fun deleteContact_onlyInRemote_throwsException() {
        // GIVEN a remote data source with 2 records and a local source with 0
        // WHEN the repository deletes a contact
        val contact = contact2
        runBlocking { contactRepository.deleteContact(contact) }
        // THEN both data sources should throw exception
        assertNull(localDataSource.fakeDatabase[contact.id])
        assertNull(remoteDataSource.fakeDatabase[contact.id])
    }

    @Test
    fun fetchContactDetail_nominalFetchAndSave_equals() {
        // GIVEN a local data source with 0 records
        val contact = contact1
        assertThrows(NullPointerException::class.java) { runBlocking { localDataSource.getContactDetail(contact.id) } }
        // WHEN the repository fetches a remote contact
        runBlocking { contactRepository.fetchContactDetail(contact.id) }
        // THEN local repository will have that 1 record added to it
        assertEquals(contact, localDataSource.fakeDatabase[contact.id])}

    @Test
    fun fetchContactDetail_replaceOldLocalRecord_notEquals() {
        // GIVEN a remote data source with an newly revised contact
        val oldContact = contact3
        val newContact = oldContact.copy(firstName = "Abradolf", lastName = "Lincler")
        remoteDataSource.fakeDatabase[newContact.id] = newContact
        // AND a local source with an old record of that same contact id
        localDataSource.fakeDatabase[oldContact.id] = oldContact
        // WHEN the repository fetches the remote contact
        runBlocking { contactRepository.fetchContactDetail(oldContact.id) }
        // THEN local repository will have that record updated to match the new
        assertNotEquals(oldContact, localDataSource.fakeDatabase[newContact.id])
        assertEquals(newContact, localDataSource.fakeDatabase[newContact.id])
    }

    @Test
    fun getContactDetail_nominal_equals() {
        // GIVEN a local data source with a saved contact
        val contact = contact1
        runBlocking { localDataSource.saveContact(contact) }
        // WHEN a contact is observed
        val currentContact = contactRepository.getContactDetail(contact.id).getOrAwaitValue()
        // THEN the observed contact matches the saved contact
        assertEquals(contact, currentContact)
    }

    @Test
    fun getContactDetail_notPresent_throwsException() {
        // GIVEN a local data empty of records
        val contact = contact2
        // WHEN a specific contact is asked to be observed
        // THEN the an exception is thrown for LiveData never having been set, thus cant be observed
        assertThrows(TimeoutException::class.java) {
            runBlocking { contactRepository.getContactDetail(contact.id).getOrAwaitValue() }
        }
    }

    @Test
    fun postContact() {
    }

    @Test
    fun putContact() {
    }

    @Test
    fun refreshContacts_nominal_equals2() {
        // GIVEN a remote data source with 2 records and a local source with 0
        // WHEN the repository refreshes Contacts
        runBlocking { contactRepository.refreshContacts() }
        // THEN local repository will have 2 records
        val localSize = runBlocking { localDataSource.getContacts().size }
        assertEquals(2, localSize)
    }

    @Test
    fun refreshContacts_nominal_equals3() {
        // GIVEN a remote data source with 2 records
        //       and a local source with 1 unique record, different from the other 2
        runBlocking { localDataSource.saveContact(contact3) }
        // WHEN the repository refreshes Contacts
        runBlocking { contactRepository.refreshContacts() }
        // THEN local repository will have 2 records
        val localSize = runBlocking { localDataSource.getContacts().size }
        assertEquals(3, localSize)
    }

    @Test
    fun refreshContacts_nominal_notEquals3() {
        // GIVEN a remote data source with 2 records
        //       and a local source with 1 record (common to local & remote sources)
        runBlocking { localDataSource.saveContact(contact2) }
        // WHEN the repository refreshes Contacts
        runBlocking { contactRepository.refreshContacts() }
        // THEN local repository will have 2 records
        val localSize = runBlocking { localDataSource.getContacts().size }
        assertEquals(2, localSize)
    }
}