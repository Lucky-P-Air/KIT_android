package com.example.kit.ui.contactdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kit.R
import com.example.kit.databinding.FragmentContactDetailBinding
import com.example.kit.ui.contactlist.ContactListViewModel
import com.example.kit.utils.formatLocalDates
import com.example.kit.utils.getNextContactLocalDate

private const val TAG = "ContactDetailFragment"

class ContactDetailFragment : Fragment() {

    companion object {
        //fun newInstance() = ContactDetailFragment()
    }

    private val viewModel: ContactListViewModel by activityViewModels()
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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_detail, container, false)
        // val currentContext = view.context
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            contactListViewModel = viewModel
            contactDetailFragment = this@ContactDetailFragment
            currentContact = viewModel.currentContact.value
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ContactDetail View Created")
        binding.apply {
            //TODO Test if this updates live with contact detail changes/updates or if XML databinding is needed
            cardDetailLastContact.text = getString(
                R.string.last_contact_date,
                (currentContact!!.lastContacted?.let { formatLocalDates(it) }) ?: "Never"
            )
            cardDetailNextContact.text = getString(
                R.string.next_contact_date,
                formatLocalDates(getNextContactLocalDate(currentContact!!))
            )
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
}