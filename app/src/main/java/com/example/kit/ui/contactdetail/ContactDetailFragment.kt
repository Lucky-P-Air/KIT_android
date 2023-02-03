package com.example.kit.ui.contactdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kit.databinding.FragmentContactDetailBinding

class ContactDetailFragment : Fragment() {

    companion object {
        fun newInstance() = ContactDetailFragment()
    }

    private lateinit var viewModel: ContactDetailViewModel
    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize ViewModel
        val contactDetailViewModel = ViewModelProvider(this).get(ContactDetailViewModel::class.java)

        // Initialize View Binding
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)

        // Make reference to Header/Title TextView, & Button
        val headerTextView: TextView = binding.textContactDetail

        // Observe ViewModel state and retrieve text data based on current state
        contactDetailViewModel.text.observe(viewLifecycleOwner) {
            headerTextView.text = it
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}