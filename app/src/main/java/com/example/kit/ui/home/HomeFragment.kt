package com.example.kit.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kit.databinding.FragmentHomeBinding
private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observer to retrieve text from HomeViewModel
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // enableAppBarMenu()  // Used to apply AppBar Menu to this fragment only

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "Home view destroyed")
    }

    /* Currently being used at the MainActivity Level instead of Fragments
    private fun enableAppBarMenu() {
        // CODE SNIPPET FOR REPLACING DEPRECATED setHasOptionsMenu()

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()
        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.app_bar_menu, menu)
                Log.d(TAG,"onCreateMenu called")
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                Log.d(TAG,"onMenuItemSelected called")
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    */
}