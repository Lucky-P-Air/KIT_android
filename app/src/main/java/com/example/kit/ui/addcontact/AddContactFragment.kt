package com.example.kit.ui.addcontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kit.R
import com.example.kit.databinding.FragmentAddContactBinding

class AddContactFragment : Fragment() {

    companion object {
        fun newInstance() = AddContactFragment()
    }

    private val viewModel: AddContactViewModel by viewModels()
    private lateinit var binding: FragmentAddContactBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize Data Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_contact, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.addContactViewModel = viewModel

    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddContactViewModel::class.java)
        // TODO: Use the ViewModel
    }
    */
}