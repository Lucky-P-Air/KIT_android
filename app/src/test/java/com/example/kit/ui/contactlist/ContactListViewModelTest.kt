package com.example.kit.ui.contactlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
     * Test Naming convention is
     * [functionName]_[descriptorOfTestCondition]_[expectedResult] ()
     */

    private lateinit var viewModel: ContactListViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun getViewModel() {
        viewModel = ContactListViewModel(ApplicationProvider.getApplicationContext())
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
        assertFalse(viewModel.errorPhoneNumber("3050285759"))
        assertFalse(viewModel.errorPhoneNumber("0000000000"))
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

}




