package com.example.kit.ui.contactdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.example.kit.R
import com.example.kit.databinding.FragmentContactDetailBinding
import com.example.kit.model.Contact
import com.example.kit.model.DatabaseContact
import com.example.kit.model.asContact
import com.example.kit.ui.contactlist.ContactListViewModel
import com.example.kit.ui.contactlist.ContactListViewModelFactory
import com.example.kit.utils.formatLocalDateTimes
import com.example.kit.utils.getNextContactLocalDateTime

private const val TAG = "ContactDetailFragment"

class ContactDetailFragment : Fragment() {

    companion object {
        //fun newInstance() = ContactDetailFragment()
    }

    //private val viewModel: ContactListViewModel by activityViewModels()
    // View Model & Data Binding Declarations
    private val viewModel: ContactListViewModel by activityViewModels {
        ContactListViewModelFactory(
            requireNotNull(this.activity).application)
    }

    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseContact: LiveData<DatabaseContact>
    private lateinit var liveContact: LiveData<Contact>
    private lateinit var currentContact: Contact
    private lateinit var id: String

    private val contactObserver = Observer<Contact> {
        Log.d(TAG, "Contact ${it.id} : ${it.firstName}")
        currentContact = it
        bindContact()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize Data Binding
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_contact_detail, container, false)
        // val currentContext = view.context
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            contactListViewModel = viewModel
            contactDetailFragment = this@ContactDetailFragment
        }
        id = viewModel.currentId
        databaseContact = viewModel.getDatabaseContactDetail(id)
        liveContact = databaseContact.map { it.asContact() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ContactDetail View Created")
        liveContact.observe(viewLifecycleOwner, contactObserver)
    }

    private fun bindContact() {
        binding.apply {
            cardDetailName.text = getString(R.string.text_fullname, currentContact.firstName, currentContact.lastName)
            cardDetailStatus.text = currentContact.status.let {
                    it.replaceFirstChar {char -> char.uppercase()}
            }
            cardDetailLastContact.text = getString(R.string.last_contact_date,
                currentContact.lastContacted?.let {
                        localDateTime -> formatLocalDateTimes(localDateTime) } ?: "Never"
            )
            cardDetailNextContact.text = getString(R.string.next_contact_date,
                formatLocalDateTimes(getNextContactLocalDateTime(currentContact))
            )
            cardDetailEmail.text = currentContact.email
            cardDetailPhone.text = currentContact.phoneNumber
            checkBox.isChecked = currentContact.remindersEnabled
        }
    }

    fun deleteContact() {
        //TODO: Insert a confirmation dialog before executing the rest of this logic
        Log.d(TAG, "Observing to delete ${currentContact.id}")
        try { viewModel.deleteContact(databaseContact.value!!) }
        catch (e: Exception) {
            Toast.makeText(this.requireContext(), R.string.toast_contact_not_deleted, Toast.LENGTH_SHORT).show()
        }
        goToContactList()
    }

    fun goToEditContact() {
        findNavController().navigate(R.id.action_contactDetailFragment_to_editContactFragment)
    }

    private fun goToContactList() {
        findNavController().navigate(R.id.action_contactDetailFragment_to_navigation_contactlist)
    }

    fun markDone() {
        try {
            viewModel.markContacted(currentContact)
        } catch (e: Exception) {
            Toast.makeText(this.requireContext(), R.string.toast_contact_not_updated, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ContactDetail Fragment Destroyed")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "ContactDetail Fragment's View Destroyed")
    }
}