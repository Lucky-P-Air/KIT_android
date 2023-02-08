package com.example.kit.ui.contactdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.kit.R
import com.example.kit.databinding.FragmentContactDetailBinding
import com.example.kit.ui.contactlist.ContactListViewModel

private const val TAG = "ContactDetailFragment"
class ContactDetailFragment : Fragment() {

    companion object {
        fun newInstance() = ContactDetailFragment()
        const val POSITION = "position"
    }

    // private val viewModel: ContactDetailViewModel by viewModels()
    private val viewModel: ContactListViewModel by activityViewModels()
    private lateinit var binding: FragmentContactDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val position: Int = it.getInt(POSITION)
            Log.d(TAG, "Position $position given from Recycler")
            // TODO: Use position value to retrieve Contact details from data source
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Data Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_detail, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.contactListViewModel = viewModel
    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }
    */
}