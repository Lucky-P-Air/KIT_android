package com.example.kit.ui.contactdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.kit.R
import com.example.kit.databinding.FragmentContactDetailBinding
import com.example.kit.ui.contactlist.ContactListViewModel
import com.example.kit.utils.formatLocalDates
import com.example.kit.utils.getNextContactLocalDate

private const val TAG = "ContactDetailFragment"
private const val POSITION = "position"

class ContactDetailFragment : Fragment() {

    companion object {
        fun newInstance() = ContactDetailFragment()
    }

    private val viewModel: ContactListViewModel by activityViewModels()
    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!
    private var position: Int = 1  // Hacky. This doesn't need initializing here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Capture SafeArgs
        arguments?.let {
            position = it.getInt(POSITION)
            Log.d(TAG, "Position $position given from Recycler")
        }
        // Set which contact from list will be Detailed. Could be moved to onViewCreated
        // TODO: Replace this position indexing with a contact primary key
        // Removed after ListAdapter binding
        //viewModel.setCurrentContact(position)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize Data Binding
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_detail, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // val currentContext = view.context
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            contactListViewModel = viewModel
            contactDetailFragment = this@ContactDetailFragment
            currentContact = viewModel.currentContact.value
            cardDetailButtonEdit.setOnClickListener {
                val action =
                    ContactDetailFragmentDirections.actionContactDetailFragmentToEditContactFragment()
                view.findNavController().navigate(action)
            }
            statusString = currentContact!!.status.replaceFirstChar { it.uppercase() }

            //TODO Test if this updates live with contact detail changes/updates or if XML databinding is needed
            cardDetailLastContact.text = getString(
                R.string.last_contact_date,
                (currentContact!!.lastContacted?.let {formatLocalDates(it)}) ?: "Never"
            )
            cardDetailNextContact.text = getString(
                R.string.next_contact_date,
                formatLocalDates(getNextContactLocalDate(currentContact!!))
            )
        }
        //viewModel.setCurrentContact(position)
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

    private fun goToContactList() {
        findNavController().navigate(R.id.action_contactDetailFragment_to_navigation_contactlist)
    }
}