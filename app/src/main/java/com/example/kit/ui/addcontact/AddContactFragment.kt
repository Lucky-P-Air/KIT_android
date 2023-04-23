package com.example.kit.ui.addcontact

import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
        Log.d("AddContactFragment", "onSubmitted called")

        // Validate non-null inputs for required fields
        // TODO: Still needs input/value validation on emails
        if (errorFirstName() or errorPhoneNumber() or errorEmail() or errorIntervalTime() ) return

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
            goToContactList() // TODO replace goToContactList() with goToContactDetail() of newly added contact
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

    private fun errorEmail(): Boolean {
        /**
         * Return 'true' if there's an error in the email format.
         * Format-pattern-matching via Patterns.EMAIL_ADDRESS
         */
        val emailValue = binding.textInputAddContactEmail.text.toString()
        if (emailValue.isEmpty()) return false
        // Return true if emailValue does not match EMAIL_ADDRESS pattern
        return if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            binding.textLayoutAddContactEmail.isErrorEnabled = true
            binding.textLayoutAddContactEmail.error = getString(R.string.error_email)
            true
        } else {
            binding.textLayoutAddContactEmail.isErrorEnabled = false
            binding.textLayoutAddContactEmail.error = null
            false
        }
    }

    private fun errorFirstName(): Boolean {
        val firstNameValue = binding.textInputAddContactFirstName.text
        if (firstNameValue.isNullOrEmpty()) {
            binding.textLayoutAddContactFirstName.isErrorEnabled = true
            binding.textLayoutAddContactFirstName.error = getString(R.string.error_first_name)
            return true
        } else {
            binding.textLayoutAddContactFirstName.isErrorEnabled = false
            binding.textLayoutAddContactFirstName.error = null
        }
        return false
    }

    private fun errorIntervalTime(): Boolean {
        val intervalTimeValue = binding.textInputAddContactIntervalTime.text
        if (intervalTimeValue.isNullOrEmpty()) {
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

    private fun errorPhoneNumber(): Boolean {
        /**
         * Return 'true' if there's an error in the phone number format.
         *
         * Django backend API requires phone numbers to have:
         *   +1 Country Code (only one currently compatible)
         *   10 numerical digits
         */
        fun isValidLength(phone: String, length: Int = 10): Boolean {
            return phone.length == length
        }

        val phoneValue = binding.textInputAddContactPhone.text.toString()

        if (phoneValue.isEmpty()) return false
        // Check string values for provided phone number. Reject any with Country Codes operator +
        when (phoneValue.first()) {
            '+' -> {
                Log.d("AddContactFragment", "Phone number ($phoneValue) has prohibited + character")
                binding.textLayoutAddContactPhone.isErrorEnabled = true
                binding.textLayoutAddContactPhone.error = getString(R.string.error_phone_number)
                return true
            }
        }

        if (isValidLength(phoneValue)) {
            Log.d(
                "AddContactFragment",
                "Valid 10-digit phone number ($phoneValue) provided. Will add country code +1"
            )
            binding.textLayoutAddContactPhone.isErrorEnabled = false
            binding.textLayoutAddContactPhone.error = null
        } else {
            Log.d("AddContactFragment", "Phone number ($phoneValue) is an invalid length")
            binding.textLayoutAddContactPhone.isErrorEnabled = true
            binding.textLayoutAddContactPhone.error = getString(R.string.error_phone_number)
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AddContactFragment", "Add Contact Fragment destroyed")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("AddContactFragment", "Add Contact Fragment's View destroyed")
    }

}