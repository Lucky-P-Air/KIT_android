package com.example.kit

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kit.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Reference to Bottom Nav View in MainActivity's layout
        val navView: BottomNavigationView = binding.navView
        Log.d("MainActivity", "ActivityCreated. Bindings complete")

        // Create reference to NavHostFragment, and get its NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

//        val appBarConfiguration = AppBarConfiguration(navController.graph)  // This version forces Up to return Home
        val appBarConfiguration = AppBarConfiguration(
            setOf<Int>(R.id.navigation_home,R.id.navigation_contactlist, R.id.navigation_addContact)
        )
        // Adds up/back button on ActionBar. But this conflicts with AppBarMenu
        setupActionBarWithNavController(navController, appBarConfiguration)
        // enableAppBarMenu() // Using this breaks the up button in ActionBar

        // Setup of bottom navigation NavView
        navView.setupWithNavController(navController)
        Log.d(TAG, "navView setup successfully")
    }

    // Enable 'Navigate Up', or the in-app Back button (Not the Android button)
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun enableAppBarMenu() {
        // CODE SNIPPET FOR REPLACING DEPRECATED setHasOptionsMenu() used within Fragments

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = this //requireActivity() if used in Fragments
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
                when (menuItem.itemId) {

                    //TODO: Implement navigation to user account settings page
                    R.id.action_account_settings -> {
                        Log.d(TAG, "Account settings icon clicked")
                        Log.d(
                            TAG,
                            "onMenuItemSelected called on ${resources.getResourceName(menuItem.itemId)}"
                        )
                        return true
                    }
                    //TODO: get the resource id of the up button to trigger navigateUp
                    androidx.appcompat.R.id.home -> {
                        Log.d(
                            TAG,
                            "Back Button. onMenuItemSelected called on ${resources.getResourceName(menuItem.itemId)}"
                        )
                        return true
                    }
                    else -> {

                        Log.d(TAG, "onMenuItemSelected called on $menuItem")
                        Log.d(TAG, "onMenuItemSelected called on ${menuItem.itemId}")
                        Log.d(
                            TAG,
                            "onMenuItemSelected called on ${resources.getResourceName(menuItem.itemId)}"
                        )
                        return true
                    }
                }
            }
        }, ViewTreeLifecycleOwner.get(binding.root)!!, Lifecycle.State.STARTED)
        // replace binding.root with another view if used within a Fragment or subview
    }
}