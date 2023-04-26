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

    private lateinit var viewModel: ContactListViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun getViewModel() {
        viewModel = ContactListViewModel(ApplicationProvider.getApplicationContext())
    }



    // Following tests should raise "false" flags, indicating valid input
    @Test
    fun errorEmail_nominal() {
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
    fun errorEmail_name_split() {
        /**
         * errorEmail() test for email address with . between names before @
         */
        assertFalse(viewModel.errorEmail("My.Name@domain.com"))
    }

    @Test
    fun errorEmail_country() {
        /**
         * errorEmail() test for email address with country extension
         * (e.g. @domain.com.br)
         */
        assertFalse(viewModel.errorEmail("MyName@domain.com.br"))
    }

    // Following tests should raise "true" flags, indicating invalid input
    @Test
    fun errorEmail_no_at() {
        /**
         * errorEmail() test for email address without @ character
         */
        assertTrue(viewModel.errorEmail("MyNamedomain.com"))
    }
    @Test
    fun errorEmail_no_domain() {
        /**
         * errorEmail() test for email address without domain after @ character
         */
        assertTrue(viewModel.errorEmail("MyName@.com"))
    }
    @Test
    fun errorEmail_no_domain_extension_split() {
        /**
         * errorEmail() test for email address without '.' between domain and extension
         */
        assertTrue(viewModel.errorEmail("MyName@domaincom"))
    }
    @Test
    fun errorEmail_extension_dot() {
        /**
         * errorEmail() test for email address with ending '.' after extension
         */
        assertTrue(viewModel.errorEmail("MyName@domain.com."))
    }

}




