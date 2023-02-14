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

class AddContactFragment : Fragment() {

    companion object {
        fun newInstance() = AddContactFragment()
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
                false)
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
        //TODO: Still needs input/value validation on emails/phone?
        Log.d("AddContactFragment", "onSubmitted called")

        // Validate non-null inputs for required fields
        if (errorFirstName() or errorIntervalTime()) return

        viewModel.addContact(
            binding.textInputAddContactFirstName.text.toString(),
            binding.textInputAddContactLastName.text.toString(),
            binding.textInputAddContactPhone.text.toString(),
            binding.textInputAddContactEmail.text.toString(),
            binding.textInputAddContactIntervalTime.text.toString().toInt(),
            binding.spinnerIntervalUnit.selectedItem.toString().lowercase(),
        )
        Toast.makeText(this.requireContext(), R.string.toast_contact_added, Toast.LENGTH_SHORT).show()
        goToContactList()
    }

    private fun goToContactList() {
        findNavController().navigate(R.id.action_navigation_addContact_to_navigation_contactlist)
    }

    private fun errorFirstName() : Boolean {
        val firstNameValue = binding.textInputAddContactFirstName.text
        if (firstNameValue.isNullOrEmpty()) {
            binding.textLayoutAddContactFirstName.isErrorEnabled = true
            binding.textLayoutAddContactFirstName.error = getString(R.string.error_first_name)
            Log.d("AddContactFragment", "First Name value is null or empty")
            return true
        } else{
            binding.textLayoutAddContactFirstName.isErrorEnabled = false
            binding.textLayoutAddContactFirstName.error = null
        }
        return false
    }

    private fun errorIntervalTime() : Boolean {
        val intervalTimeValue = binding.textInputAddContactIntervalTime.text
        if (intervalTimeValue.isNullOrEmpty()) {
            binding.textLayoutAddContactIntervalTime.isErrorEnabled = true
            binding.textLayoutAddContactIntervalTime.error = getString(R.string.error_interval_number)
            Log.d("AddContactFragment", "IntervalTime value is null or empty")
            return true
        } else{
            binding.textLayoutAddContactIntervalTime.isErrorEnabled = false
            binding.textLayoutAddContactIntervalTime.error = null
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