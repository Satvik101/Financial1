package com.fincalc.app.navigation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fincalc.app.R
import com.fincalc.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        val mainTabs = setOf(
            R.id.homeFragment,
            R.id.historyFragment,
            R.id.goalsFragment,
            R.id.settingsFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val showBottomNav = destination.id in mainTabs
            binding.bottomNav.visibility = if (showBottomNav) View.VISIBLE else View.GONE
            val params = binding.navHostFragment.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = if (showBottomNav) {
                resources.getDimensionPixelSize(R.dimen.bottom_nav_height)
            } else {
                0
            }
            binding.navHostFragment.layoutParams = params
        }
    }
}
