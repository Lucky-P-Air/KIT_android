package com.example.kit.ui.addcontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kit.databinding.FragmentAddContactBinding

class AddContactFragment : Fragment() {

    companion object {
        fun newInstance() = AddContactFragment()
    }

    private lateinit var viewModel: AddContactViewModel
    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel
        val addContactViewModel = ViewModelProvider(this).get(AddContactViewModel::class.java)

        // Initialize View Binding
        _binding = FragmentAddContactBinding.inflate(inflater, container, false)

        // Make reference to Header/Title TextView
        val headerTextView: TextView = binding.textAddContact

        // Observe ViewModel state and retrieve text data based on current state
        addContactViewModel.text.observe(viewLifecycleOwner) {
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
        viewModel = ViewModelProvider(this).get(AddContactViewModel::class.java)
        // TODO: Use the ViewModel
    }

}