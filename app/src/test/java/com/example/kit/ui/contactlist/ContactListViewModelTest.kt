package com.example.kit.ui.contactlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kit.ServiceLocator
import com.example.kit.data.FakeTestRepository
import com.example.kit.model.Contact
import com.example.kit.utils.getTimeNowString
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactListViewModelTest {
    /**
     * Tests for ContactListViewModel functions.
     * Test Naming convention is:
     *      functionName_descriptorOfTestCondition_expectedResult ()
     */

    private lateinit var viewModel: ContactListViewModel
    private lateinit var testRepository: FakeTestRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        testRepository = FakeTestRepository()
        ServiceLocator.contactRepository = testRepository
        val fakeTime = getTimeNowString()
        val contact1 = Contact("1", "Michael", "Scott", null, null, 2, "days", true, fakeTime, fakeTime, fakeTime, "")
        val contact2 = Contact("2", "Pam", "Beasley", null, null, 2, "days", true, fakeTime, fakeTime, fakeTime, "")
        val contact3 = Contact("3", "Princess", "Zelda", null, null, 2, "days", true, fakeTime, fakeTime, fakeTime, "")
        for (person in listOf(contact1, contact2, contact3)) {
            runBlocking{ testRepository.putContact(person) }
        }
        viewModel = ContactListViewModel(testRepository)
    }

    // Following tests should raise "false" flags, indicating valid input
    @Test
    fun errorEmail_nominal_false() {
        /**
         * errorEmail() test for valid, typical email address
         * (Including .com, .gov, .org, .edu, .me)
         */
        assertFalse(
            viewModel.errorEmail("MyName@domain.com") and
            viewModel.errorEmail("MyName@domain.gov") and
            viewModel.errorEmail("MyName@domain.edu") and
            viewModel.errorEmail("MyName@domain.org") and
            viewModel.errorEmail("MyName@domain.me")
        )
    }
    @Test
    fun errorEmail_nameSplit_false() {
        /**
         * errorEmail() test for email address with . between names before @
         */
        assertFalse(viewModel.errorEmail("My.Name@domain.com"))
    }

    @Test
    fun errorEmail_country_false() {
        /**
         * errorEmail() test for email address with country extension
         * (e.g. @domain.com.br)
         */
        assertFalse(viewModel.errorEmail("MyName@domain.com.br"))
    }

    // Following tests should raise "true" flags, indicating invalid input
    @Test
    fun errorEmail_noAt_true() {
        /**
         * errorEmail() test for email address without @ character
         */
        assertTrue(viewModel.errorEmail("MyNamedomain.com"))
    }
    @Test
    fun errorEmail_noDomain_true() {
        /**
         * errorEmail() test for email address without domain after @ character
         */
        assertTrue(viewModel.errorEmail("MyName@.com"))
    }
    @Test
    fun errorEmail_noDomainExtensionSplit_true() {
        /**
         * errorEmail() test for email address without '.' between domain and extension
         */
        assertTrue(viewModel.errorEmail("MyName@domaincom"))
    }
    @Test
    fun errorEmail_extensionDot_true() {
        /**
         * errorEmail() test for email address with ending '.' after extension
         */
        assertTrue(viewModel.errorEmail("MyName@domain.com."))
    }

    // Following tests should raise "false" flags, indicating valid input
    @Test
    fun errorPhoneNumber_nominal_false() {
        /**
         * errorPhoneNumber() test for typical 10-digit phone number
         */
        assertFalse(viewModel.errorPhoneNumber("0000000000"))
        assertFalse(viewModel.errorPhoneNumber("2222222222"))
        assertFalse(viewModel.errorPhoneNumber("3050285759"))
        assertFalse(viewModel.errorPhoneNumber("4000000000"))
        assertFalse(viewModel.errorPhoneNumber("5000000000"))
        assertFalse(viewModel.errorPhoneNumber("6000000000"))
        assertFalse(viewModel.errorPhoneNumber("7000000000"))
        assertFalse(viewModel.errorPhoneNumber("8000000000"))
        assertFalse(viewModel.errorPhoneNumber("9000000000"))
    }

    // Following tests should raise "true" flags, indicating invalid input
    @Test
    fun errorPhoneNumber_blank_true() {
        /**
         * errorPhoneNumber() test for a blank phone entry.
         * There are currently checks (in Fragments) to
         * prevent blanks from reaching this function.
         */
        assertTrue(viewModel.errorPhoneNumber(""))
    }

    @Test
    fun errorPhoneNumber_wrongLength_true() {
        /**
         * errorPhoneNumber() test for phone with incorrect length
         */
        assertTrue(viewModel.errorPhoneNumber("0"))
        assertTrue(viewModel.errorPhoneNumber("22"))
        assertTrue(viewModel.errorPhoneNumber("333333333"))
        assertTrue(viewModel.errorPhoneNumber("33333333344"))
    }
    @Test
    fun errorPhoneNumber_countryCode_true() {
        /**
         * errorPhoneNumber() test for phone with country code included
         */
        assertTrue(viewModel.errorPhoneNumber("+133333334"))
        assertTrue(viewModel.errorPhoneNumber("+10133333334"))
        assertTrue(viewModel.errorPhoneNumber("+2013333333"))
        assertTrue(viewModel.errorPhoneNumber("+20133333"))
    }

    @Test
    fun errorPhoneNumber_areaCode1_true() {
        /**
         * errorPhoneNumber() test for phone beginning with 1.
         * 1 is not the first-digit of any valid US area code
         */
        assertTrue(viewModel.errorPhoneNumber("1053333333"))
        assertTrue(viewModel.errorPhoneNumber("1000000000"))
        assertTrue(viewModel.errorPhoneNumber("1015555555"))
    }

}




