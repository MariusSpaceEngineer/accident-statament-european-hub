package com.inetum.realdolmen.crashkit.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    private val securedPreferences = CrashKitApp.securedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navController = findNavController(R.id.fragmentContainerView)
        navController.setGraph(R.navigation.bottom_navigation_bar)
        bottomNavigationView.setupWithNavController(navController)

        savedInstanceState?.let {
            navController.restoreState(it.getBundle("nav_state"))
        }

        if (securedPreferences.isGuest()) {
            val menu = binding.bottomNavigationView.menu
            menu.removeItem(R.id.profileFragment)
        }

        setupNavigation(navController, binding)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save the NavController's state
        outState.putBundle("nav_state", navController.saveState())
        super.onSaveInstanceState(outState)
    }

    private fun setupNavigation(navController: NavController, binding: ActivityHomeBinding) {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.newStatementFragment -> {
                    navController.navigate(R.id.newStatementFragment)
                    true
                }

                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }

                else -> false
            }
        }
    }
}
