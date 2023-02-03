package com.example.kit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kit.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        Log.d("MainActivity", "navController defined successfully")

        /* AppBar configuration setup
           Passing each menu ID as a set of Ids because each
           menu should be considered as top level destinations. */
        //TODO Check if this is working?
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_contactlist, R.id.navigation_addContact))
        setupActionBarWithNavController(navController, appBarConfiguration)
        Log.d("MainActivity", "ActionBar Setup successfully")

        // Setup of bottom navigation NavView
        navView.setupWithNavController(navController)
        Log.d("MainActivity", "navView setup successfully")
    }

    // Enable 'Navigate Up', or the in-app Back button (Not the Android button)
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}