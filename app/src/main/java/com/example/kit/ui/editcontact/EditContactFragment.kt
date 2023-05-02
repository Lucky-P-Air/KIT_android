package com.example.kit.ui.editcontact

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
import androidx.navigation.fragment.findNavController
import com.example.kit.BaseApplication
import com.example.kit.R
import com.example.kit.databinding.FragmentEditContactBinding
import com.example.kit.model.Contact
import com.example.kit.ui.contactlist.ContactListViewModel
import com.example.kit.ui.contactlist.ContactListViewModelFactory

/**
 * A simple [Fragment] subclass.
 * Use the [EditContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val TAG = "EditContactFragment"

class EditContactFragment : Fragment() {
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment EditContactFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = EditContactFragment()
    }

    //private val viewModel: ContactListViewModel by activityViewModels()
    // View Model & Data Binding Declarations
    private val viewModel: ContactListViewModel by activityViewModels<ContactListViewModel> {
        ContactListViewModelFactory((requireContext().applicationContext as BaseApplication).contactRepository)
    }
    private var _binding: FragmentEditContactBinding? = null
    private val binding get() = _binding!!
    private lateinit var liveContact: LiveData<Contact>
    private lateinit var currentContact: Contact
    private lateinit var id: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_contact, container, false)
        // Inflate the layout for this fragment
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            contactListViewModel = viewModel
            editContactFragment = this@EditContactFragment
        }
        id = viewModel.currentId
        liveContact = viewModel.getContactDetail(id)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "EditDetail View Created")

        val contactObserver = Observer<Contact> {
            currentContact = it
            bindContact(view)
        }

        liveContact.observe(viewLifecycleOwner, contactObserver)
    }

    private fun bindContact(view: View) {
        // Access array of possible interval units
        val intervalUnitsArray = view.context.resources
            .getStringArray(R.array.intervalUnit_array)

        binding.apply {
            textInputEditContactFirstName.setText(currentContact.firstName)
            textInputEditContactLastName.setText(currentContact.lastName)
            textInputEditContactPhone.setText(
                if (currentContact.phoneNumber.isNullOrEmpty()) {
                        null
                } else {
                    currentContact.phoneNumber?.substring(2)
                }
            )
            textInputEditContactEmail.setText(currentContact.email)
            checkBox.isChecked = currentContact.remindersEnabled
            textInputEditContactIntervalTime.setText(currentContact.intervalTime.toString())
            spinnerIntervalUnit.setSelection(
                intervalUnitsArray.indexOf(  // array of possible intervalUnits
                    currentContact.intervalUnit // Contact's saved intervalUnit
                        .replaceFirstChar { it.uppercase() })  // Capitalize it to match dropdown entries
            )
        }
    }

    fun onSubmitted() {
        Log.d(TAG, "onSubmitted called")

        if (invalidFirstName() or invalidPhoneNumber() or invalidEmail() or invalidIntervalTime()) return

        try {
            viewModel.updateContact(
                currentContact.id,
                binding.textInputEditContactFirstName.text.toString(),
                binding.textInputEditContactLastName.text.toString(),
                if (binding.textInputEditContactPhone.text.isNullOrEmpty()) {
                    ""
                } else {
                    "+1${binding.textInputEditContactPhone.text}"
                },
                binding.textInputEditContactEmail.text.toString(),
                binding.textInputEditContactIntervalTime.text.toString().toInt(),
                binding.spinnerIntervalUnit.selectedItem.toString().lowercase(),
                binding.checkBox.isChecked,
                currentContact.lastContacted,
                currentContact.createdAt,
                currentContact.updatedAt,
                currentContact.status
            )
            Toast.makeText(this.requireContext(), R.string.toast_contact_updated, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this.requireContext(),R.string.toast_contact_not_updated,Toast.LENGTH_LONG).show()
        }
        goToContactDetail()
    }

    private fun goToContactDetail() {
        val action = EditContactFragmentDirections
            .actionEditContactFragmentToContactDetailFragment()
        findNavController().navigate(action)
    }

    private fun invalidEmail(): Boolean {
        /**
         * Return 'true' if there's an error in the email format.
         * Format-pattern-matching via Patterns.EMAIL_ADDRESS
         */
        val emailValue = binding.textInputEditContactEmail.text.toString()
        val isInvalid = viewModel.errorEmail(emailValue)
        if (isInvalid) {
            binding.textLayoutEditContactEmail.isErrorEnabled = true
            binding.textLayoutEditContactEmail.error = getString(R.string.error_email)
        } else {
            binding.textLayoutEditContactEmail.isErrorEnabled = false
            binding.textLayoutEditContactEmail.error = null
        }
        return isInvalid
    }

    private fun invalidFirstName(): Boolean {
        val firstNameValue = binding.textInputEditContactFirstName.text.toString()
        if (firstNameValue.isEmpty()) {
            binding.textLayoutEditContactFirstName.isErrorEnabled = true
            binding.textLayoutEditContactFirstName.error = getString(R.string.error_first_name)
            return true
        } else {
            binding.textLayoutEditContactFirstName.isErrorEnabled = false
            binding.textLayoutEditContactFirstName.error = null
        }
        return false
    }

    private fun invalidIntervalTime(): Boolean {
        val intervalTimeValue = binding.textInputEditContactIntervalTime.text.toString()
        if (intervalTimeValue.isEmpty()) {
            binding.textLayoutEditContactIntervalTime.isErrorEnabled = true
            binding.textLayoutEditContactIntervalTime.error =
                getString(R.string.error_interval_number)
            return true
        } else {
            binding.textLayoutEditContactIntervalTime.isErrorEnabled = false
            binding.textLayoutEditContactIntervalTime.error = null
        }
        return false
    }

    private fun invalidPhoneNumber(): Boolean {
        /**
         * Return 'true' if there's an error in the phone number format.
         *
         * Django backend API requires phone numbers to have:
         *   +1 Country Code (only one currently compatible)
         *   10 numerical digits
         */
        val phoneValue = binding.textInputEditContactPhone.text.toString()
        if (phoneValue.isEmpty()) return false
        val isInvalid = viewModel.errorPhoneNumber(phoneValue)

        // Set or reset errors
        if (isInvalid) {
            binding.textLayoutEditContactPhone.isErrorEnabled = true
            binding.textLayoutEditContactPhone.error = getString(R.string.error_phone_number)
        } else {
            binding.textLayoutEditContactPhone.isErrorEnabled = false
            binding.textLayoutEditContactPhone.error = null
        }
        return isInvalid
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Edit Contact fragment destroyed")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "Edit Contact fragment's View destroyed")
        _binding = null
    }

}