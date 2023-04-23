package com.example.kit.ui.addcontact

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kit.R
import com.example.kit.databinding.FragmentAddContactBinding
import com.example.kit.ui.contactlist.ContactListViewModel

private const val TAG = "AddContactFragment"

class AddContactFragment : Fragment() {

    companion object {
        // fun newInstance() = AddContactFragment()
    }

    private val viewModel: ContactListViewModel by activityViewModels()
    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize Data Binding
        val fragmentBinding: FragmentAddContactBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_contact,
                container,
                false
            )
        _binding = fragmentBinding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            contactListViewModel = viewModel
            addContactFragment = this@AddContactFragment
        }
    }

    fun onSubmitted() {
        Log.d(TAG, "onSubmitted called")

        // Escape if invalid inputs provided
        if (invalidFirstName() or invalidPhoneNumber() or invalidEmail() or invalidIntervalTime()) return

        try {
            viewModel.addContact(
                binding.textInputAddContactFirstName.text.toString(),
                binding.textInputAddContactLastName.text.toString(),
                if (binding.textInputAddContactPhone.text.isNullOrEmpty()) {
                    ""
                } else {
                    "+1${binding.textInputAddContactPhone.text}"
                },
                binding.textInputAddContactEmail.text.toString(), // Add +1 Country Code
                binding.textInputAddContactIntervalTime.text.toString().toInt(),
                binding.spinnerIntervalUnit.selectedItem.toString().lowercase(),
            )
        } catch (e: Exception) {
            Toast.makeText(this.requireContext(), R.string.toast_contact_not_added, Toast.LENGTH_SHORT).show()
        }
        /*
        if (submittalSuccess) {
            Toast.makeText(this.requireContext(), R.string.toast_contact_added, Toast.LENGTH_SHORT).show()
            //goToContactDetail()
            goToContactList()
        } else {
            Toast.makeText(this.requireContext(), R.string.toast_contact_not_added, Toast.LENGTH_SHORT).show()
            goToContactList()
        }
        */
        goToContactList()
        //goToContactDetail()
    }

    private fun goToContactDetail() {
        findNavController().navigate(R.id.action_navigation_addContact_to_contactDetailFragment)
    }

    private fun goToContactList() {
        findNavController().navigate(R.id.action_navigation_addContact_to_navigation_contactlist)
    }

    private fun invalidEmail(): Boolean {
        /**
         * Return 'true' if there's an error in the email format.
         * Format-pattern-matching via Patterns.EMAIL_ADDRESS
         */
        val emailValue = binding.textInputAddContactEmail.text.toString()
        val isInvalid = viewModel.errorEmail(emailValue)
        if (isInvalid) {
            binding.textLayoutAddContactEmail.isErrorEnabled = true
            binding.textLayoutAddContactEmail.error = getString(R.string.error_email)
        } else {
            binding.textLayoutAddContactEmail.isErrorEnabled = false
            binding.textLayoutAddContactEmail.error = null
        }
        return isInvalid
    }

    private fun invalidFirstName(): Boolean {
        val firstNameValue = binding.textInputAddContactFirstName.text.toString()
        if (firstNameValue.isEmpty()) {
            binding.textLayoutAddContactFirstName.isErrorEnabled = true
            binding.textLayoutAddContactFirstName.error = getString(R.string.error_first_name)
            return true
        } else {
            binding.textLayoutAddContactFirstName.isErrorEnabled = false
            binding.textLayoutAddContactFirstName.error = null
        }
        return false
    }

    private fun invalidIntervalTime(): Boolean {
        val intervalTimeValue = binding.textInputAddContactIntervalTime.text.toString()
        if (intervalTimeValue.isEmpty()) {
            binding.textLayoutAddContactIntervalTime.isErrorEnabled = true
            binding.textLayoutAddContactIntervalTime.error =
                getString(R.string.error_interval_number)
            return true
        } else {
            binding.textLayoutAddContactIntervalTime.isErrorEnabled = false
            binding.textLayoutAddContactIntervalTime.error = null
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
        val phoneValue = binding.textInputAddContactPhone.text.toString()
        if (phoneValue.isEmpty()) return false
        val isInvalid = viewModel.errorPhoneNumber(phoneValue)

        // Set or reset errors
        if (isInvalid) {
            binding.textLayoutAddContactPhone.isErrorEnabled = true
            binding.textLayoutAddContactPhone.error = getString(R.string.error_phone_number)
        } else {
            binding.textLayoutAddContactPhone.isErrorEnabled = false
            binding.textLayoutAddContactPhone.error = null
        }
        return isInvalid
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Add Contact Fragment destroyed")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "Add Contact Fragment's View destroyed")
    }

}