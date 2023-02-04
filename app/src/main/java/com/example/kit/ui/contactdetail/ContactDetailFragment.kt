package com.example.kit.ui.contactdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kit.R
import com.example.kit.databinding.FragmentContactDetailBinding

class ContactDetailFragment : Fragment() {

    companion object {
        fun newInstance() = ContactDetailFragment()
    }

    private val viewModel: ContactDetailViewModel by viewModels()
    private lateinit var binding: FragmentContactDetailBinding

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
        binding.contactDetailViewModel = viewModel
    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }
    */
}