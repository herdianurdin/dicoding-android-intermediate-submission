package com.saeware.storyapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.saeware.storyapp.R
import com.saeware.storyapp.databinding.FragmentSettingsBinding
import com.saeware.storyapp.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.settings)
        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        binding?.apply {
            btnLanguageSetting.setOnClickListener { handleLanguageSetting() }
            btnLogout.setOnClickListener { handleLogout() }
        }
    }

    private fun handleLanguageSetting() {
        requireActivity().startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    private fun handleLogout() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.logout)
            setMessage(R.string.confirmation_message)
            setPositiveButton(R.string.yes) { _, _ ->
                run {
                    viewModel.storeAuthToken("")
                    Intent(requireContext(), AuthActivity::class.java).also { intent ->
                        startActivity(intent)
                        requireActivity().finish()
                    }

                }
            }
            setNegativeButton(R.string.no) { _, _ -> }
            create()
            show()
        }
    }
}