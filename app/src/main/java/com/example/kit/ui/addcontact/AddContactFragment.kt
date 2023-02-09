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
    private var binding: FragmentAddContactBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize Data Binding
        val fragmentBinding: FragmentAddContactBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_contact, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            contactListViewModel = viewModel
            addContactFragment = this@AddContactFragment
        }
    }

    fun onSubmitted() {
        //TODO: Still needs input/value validation. intervalTime canNOT be empty. Fatal Crash
        Log.d("AddContactFragment", "onSubmitted called")

        viewModel.addContact(
            binding!!.textInputAddContactFirstName.text.toString(),
            binding!!.textInputAddContactLastName.text.toString(),
            binding!!.textInputAddContactPhone.text.toString(),
            binding!!.textInputAddContactEmail.text.toString(),
            binding!!.textInputAddContactIntervalTime.text.toString().toInt(),
            "weeks" //TODO: Replace this placeholder with dropdown value
            //binding.textInputAddContactIntervalTime.text,
        )
        Toast.makeText(this.requireContext(), R.string.toast_contact_added, Toast.LENGTH_SHORT).show()
        goToContactList()
    }

    private fun goToContactList() {
        findNavController().navigate(R.id.action_navigation_addContact_to_navigation_contactlist)
    }
    //TODO: Modify the layout to not require entries for LastName, phone, email. They are nullable

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AddContactFragment", "Add Contact View destroyed")
        binding = null
    }

}