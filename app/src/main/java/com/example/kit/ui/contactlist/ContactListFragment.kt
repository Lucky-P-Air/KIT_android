package com.example.kit.ui.contactlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kit.R
import com.example.kit.databinding.FragmentContactListBinding

class ContactListFragment : Fragment() {

    companion object {
        fun newInstance() = ContactListFragment()
    }

    private val viewModel: ContactListViewModel by viewModels()
    private lateinit var binding: FragmentContactListBinding
    //private var _binding: FragmentContactListBinding? = null
    //private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Data Binding
        // _binding = FragmentContactListBinding.inflate(inflater, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_list, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Data Binding properties
        binding.lifecycleOwner = viewLifecycleOwner
        binding.contactListViewModel = viewModel
        // ClickListener
        binding.buttonContactDetail.setOnClickListener {
            val action = ContactListFragmentDirections.actionNavigationContactlistToContactDetailFragment()
            this.findNavController().navigate(action)
        }

        /* Replaced all this code with data binding implementation above
        // Make reference to Header/Title TextView, & Button
        val headerTextView: TextView = binding.textContactList
        val seeContactButton: Button = binding.buttonContactDetail

        // Observe ViewModel state and retrieve text data based on current state
        viewModel.text.observe(viewLifecycleOwner) {
            headerTextView.text = it
        }

        // Set ClickListener & navigation to Contact Detail
        seeContactButton.setOnClickListener {
            val action = ContactListFragmentDirections.actionNavigationContactlistToContactDetailFragment()
            this.findNavController().navigate(action)
        }
        */
    }

    /* Unnecessary with databinding?
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactListViewModel::class.java)
        // TODO: Use the ViewModel
    }
    */
}