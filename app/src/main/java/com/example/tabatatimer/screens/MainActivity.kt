package com.example.tabatatimer.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.tabatatimer.R
import com.example.tabatatimer.databinding.ActivityMainBinding
import com.example.tabatatimer.screens.settings.SettingsFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TabataTimer)
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBarWithNavController(findNavController(R.id.fragment_container))
        binding.apply {
            navigationView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.settings_nav_btn -> {
                        findNavController(R.id.fragment_container).navigate(R.id.action_listFragment_to_settingsFragment)
                    }
                    R.id.add_sequence_nav_btn->{
                        findNavController(R.id.fragment_container).navigate(R.id.action_listFragment_to_addFragment)
                    }
                    R.id.my_sequences_nav_btn->{
                        findNavController(R.id.fragment_container).navigateUp()
                    }
                }

                true

            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController=findNavController(R.id.fragment_container)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}