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
import androidx.navigation.fragment.findNavController
import com.example.kit.R
import com.example.kit.databinding.FragmentEditContactBinding
import com.example.kit.ui.contactlist.ContactListViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [EditContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

    private val viewModel: ContactListViewModel by activityViewModels()
    private var _binding: FragmentEditContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* Not necessary since ViewModel tracks the currentContact's position from ContactDetail
        arguments?.let {
            val position = it.getInt(POSITION)
            viewModel.setCurrentContact(position)
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_contact, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access array of possible interval units
        val intervalUnitsArray = view.context.resources
            .getStringArray(R.array.intervalUnit_array)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            contactListViewModel = viewModel
            editContactFragment = this@EditContactFragment
            currentContact = viewModel.currentContact.value
            spinnerIntervalUnit.setSelection(
                intervalUnitsArray.indexOf(  // array of possible intervalUnits
                    viewModel.currentContact.value!!.intervalUnit // Contact's saved intervalUnit
                        .replaceFirstChar {it.uppercase()})  // Capitalize it to match dropdown entries
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("EditContactFragment", "Edit Contact fragment destroyed")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("EditContactFragment", "Edit Contact fragment's View destroyed")
        _binding = null
    }

    fun onSubmitted() {
        //TODO: Still needs input/value validation on emails/phone?
        //TODO: Still needs implementation of the checkbox
        Log.d("EditContactFragment", "onSubmitted called")

        // Validate non-null inputs for required fields
        if (errorFirstName() or errorIntervalTime()) return

        viewModel.updateContact(
            binding.textInputEditContactFirstName.text.toString(),
            binding.textInputEditContactLastName.text.toString(),
            binding.textInputEditContactPhone.text.toString(),
            binding.textInputEditContactEmail.text.toString(),
            binding.textInputEditContactIntervalTime.text.toString().toInt(),
            binding.spinnerIntervalUnit.selectedItem.toString().lowercase(),
        )
        Toast.makeText(this.requireContext(), R.string.toast_contact_updated, Toast.LENGTH_SHORT).show()
        goToContactList()
    }

    private fun goToContactList() {
        val action = EditContactFragmentDirections
            .actionEditContactFragmentToContactDetailFragment()
        findNavController().navigate(action)
    }

    private fun errorFirstName() : Boolean {
        val firstNameValue = binding.textInputEditContactFirstName.text
        if (firstNameValue.isNullOrEmpty()) {
            binding.textLayoutEditContactFirstName.isErrorEnabled = true
            binding.textLayoutEditContactFirstName.error = getString(R.string.error_first_name)
            Log.d("EditContactFragment", "First Name value is null or empty")
            return true
        } else{
            binding.textLayoutEditContactFirstName.isErrorEnabled = false
            binding.textLayoutEditContactFirstName.error = null
        }
        return false
    }

    private fun errorIntervalTime() : Boolean {
        val intervalTimeValue = binding.textInputEditContactIntervalTime.text
        if (intervalTimeValue.isNullOrEmpty()) {
            binding.textLayoutEditContactIntervalTime.isErrorEnabled = true
            binding.textLayoutEditContactIntervalTime.error = getString(R.string.error_interval_number)
            Log.d("EditContactFragment", "IntervalTime value is null or empty")
            return true
        } else{
            binding.textLayoutEditContactIntervalTime.isErrorEnabled = false
            binding.textLayoutEditContactIntervalTime.error = null
        }
        return false
    }
}