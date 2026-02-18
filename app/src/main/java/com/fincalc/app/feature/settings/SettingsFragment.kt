package com.fincalc.app.feature.settings

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import com.fincalc.app.FinCalcApplication
import com.fincalc.app.R
import com.fincalc.app.core.base.BaseFragment
import com.fincalc.app.core.base.SimpleViewModelFactory
import com.fincalc.app.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        val app = requireActivity().application as FinCalcApplication
        SimpleViewModelFactory { SettingsViewModel(app.appModule.preferences) }
    }

    override fun setupUi(view: View) {
        _binding = FragmentSettingsBinding.bind(view)

        when (viewModel.getCurrency()) {
            "₹" -> binding.chipInr.isChecked = true
            "$" -> binding.chipUsd.isChecked = true
            "€" -> binding.chipEur.isChecked = true
            "£" -> binding.chipGbp.isChecked = true
        }

        when (viewModel.getTheme()) {
            AppCompatDelegate.MODE_NIGHT_NO -> binding.chipThemeLight.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> binding.chipThemeDark.isChecked = true
            else -> binding.chipThemeSystem.isChecked = true
        }

        binding.switchIndianFormat.isChecked = viewModel.isIndianFormat()

        binding.chipCurrency.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val symbol = when (id) {
                R.id.chipUsd -> "$"
                R.id.chipEur -> "€"
                R.id.chipGbp -> "£"
                else -> "₹"
            }
            viewModel.setCurrency(symbol)
        }

        binding.btnPremium.setOnClickListener {
            Toast.makeText(requireContext(), "Hook billing flow here", Toast.LENGTH_SHORT).show()
            viewModel.setPremium(true)
        }

        binding.chipTheme.setOnCheckedStateChangeListener { _, checkedIds ->
            val mode = when (checkedIds.firstOrNull()) {
                R.id.chipThemeLight -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.chipThemeDark -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            viewModel.setTheme(mode)
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        binding.switchIndianFormat.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIndianFormat(isChecked)
        }

        binding.btnRateApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}"))
            startActivity(intent)
        }

        binding.btnShareApp.setOnClickListener {
            val text = "Check out ${getString(R.string.app_name)}: https://play.google.com/store/apps/details?id=${requireContext().packageName}"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(shareIntent, "Share App"))
        }

        binding.root.setOnLongClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}"))
            startActivity(intent)
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
