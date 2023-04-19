package com.example.kit.ui.contactlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kit.R
import com.example.kit.databinding.FragmentContactListBinding

class ContactListFragment : Fragment() {

    companion object {
        // fun newInstance() = ContactListFragment()
    }

    // View Model & Data Binding Declarations
    private val viewModel: ContactListViewModel by activityViewModels() //viewModels()
    /*private val viewModel: ContactListViewModel by lazy {
        val activity = requireNotNull(this.activity) {}
        ViewModelProvider(this, ContactListViewModel.Factory(activity.application))
            .get(ContactListViewModel::class.java)
    }*/

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize Data Binding
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_contact_list, container, false)
        binding.contactListViewModel = viewModel

        // Set Data Binding properties
        binding.lifecycleOwner = viewLifecycleOwner
        // Declare RecyclerView
        binding.recycleContactList.layoutManager = LinearLayoutManager(context)
        binding.recycleContactList.adapter = ContactListAdapter(ContactListListener { contact ->
            viewModel.onContactClicked(contact)
            findNavController()
                .navigate(R.id.action_navigation_contactlist_to_contactDetailFragment)
        })
        viewModel.getContactList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("ContactListFragment", "ContactListFragment's View Destroyed")
        _binding = null
    }

}