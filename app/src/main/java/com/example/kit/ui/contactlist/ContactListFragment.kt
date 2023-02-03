package com.example.kit.ui.contactlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kit.databinding.FragmentContactListBinding

class ContactListFragment : Fragment() {

    companion object {
        fun newInstance() = ContactListFragment()
    }

    private lateinit var viewModel: ContactListViewModel
    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize ViewModel
        val contactListViewModel = ViewModelProvider(this).get(ContactListViewModel::class.java)

        // Initialize View Binding
        _binding = FragmentContactListBinding.inflate(inflater, container, false)

        // Make reference to Header/Title TextView, & Button
        val headerTextView: TextView = binding.textContactList
        val seeContactButton: Button = binding.buttonContactDetail

        // Observe ViewModel state and retrieve text data based on current state
        contactListViewModel.text.observe(viewLifecycleOwner) {
            headerTextView.text = it
        }

        // Set ClickListener & navigation to Contact Detail
        seeContactButton.setOnClickListener {
            val action = ContactListFragmentDirections.actionNavigationContactlistToContactDetailFragment()
            this.findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}