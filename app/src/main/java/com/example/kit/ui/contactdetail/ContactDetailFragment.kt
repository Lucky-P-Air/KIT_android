package com.example.kit.ui.contactdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.kit.R
import com.example.kit.databinding.FragmentContactDetailBinding
import com.example.kit.model.Contact
import com.example.kit.ui.contactlist.ContactListViewModel
import com.example.kit.ui.contactlist.ContactListViewModelFactory

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO Perhaps do API GET call for detailed contact here?
        //viewModel.getContactDetail(viewModel.currentContact.value!!.id)
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
        viewModel.databaseReturn.observe(viewLifecycleOwner, Observer<Contact>(){
            Log.d(TAG, "Contact ${it.id} : ${it.firstName}")
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ContactDetail View Created")
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

    fun deleteContact() {
        //TODO: Insert a confirmation dialog before executing the rest of this logic
        viewModel.deleteContact()
        goToContactList()
    }

    fun goToEditContact() {
        findNavController().navigate(R.id.action_contactDetailFragment_to_editContactFragment)
    }

    private fun goToContactList() {
        findNavController().navigate(R.id.action_contactDetailFragment_to_navigation_contactlist)
    }

    fun markDone() {
        viewModel.markContacted()
    }
}