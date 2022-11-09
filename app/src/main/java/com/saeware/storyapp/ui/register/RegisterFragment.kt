package com.saeware.storyapp.ui.register

import android.animation.AnimatorSet
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.saeware.storyapp.R
import com.saeware.storyapp.databinding.FragmentRegisterBinding
import com.saeware.storyapp.utils.AnimationUtility.setFadeViewAnimation
import com.saeware.storyapp.utils.MessageUtility.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        binding?.apply {
            btnToLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            btnRegister.setOnClickListener { handleRegister() }
        }
    }

    private fun handleRegister() {
        showLoading(true)

        val name = binding?.edtName?.text.toString().trim()
        val email = binding?.edtEmail?.text.toString().trim()
        val password = binding?.edtPassword?.text.toString().trim()

        when {
            name.isEmpty() ->
                showInputError(binding?.edtName, getString(R.string.empty_field, getString(R.string.name)))
            email.isEmpty() ->
                showInputError(binding?.edtEmail, getString(R.string.empty_field, getString(R.string.email)))
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                showInputError(binding?.edtEmail, getString(R.string.error_email))
            password.isEmpty() ->
                showInputError(binding?.edtPassword, getString(R.string.empty_field, getString(R.string.password)))
            password.length < 6 ->
                showInputError(binding?.edtPassword, getString(R.string.error_password))
            else ->
                lifecycleScope.launchWhenResumed {
                    viewModel.userRegister(name, email, password).observe(viewLifecycleOwner) { result ->
                        result.onSuccess {
                            showToast(requireContext(), getString(R.string.register_success_message))

                            findNavController().navigateUp()
                        }
                        result.onFailure {
                            showToast(requireContext(), getString(R.string.register_failed_message))
                            showLoading(false)
                        }
                    }
                }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            edtNameLayout.isEnabled = !isLoading
            edtEmailLayout.isEnabled = !isLoading
            edtPasswordLayout.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading
            btnToLogin.isEnabled = !isLoading

            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showInputError(editText: EditText?, message: String) {
        showLoading(false)
        editText?.error = message
        showToast(requireContext(), message)
    }

    private fun playAnimation() {
        val ivIllustrationRegister = setFadeViewAnimation(binding?.ivIllustrationRegister)
        val tvRegisterTitle = setFadeViewAnimation(binding?.tvRegisterTitle)
        val tvMessage = setFadeViewAnimation(binding?.tvMessage)
        val tvLabelName = setFadeViewAnimation(binding?.tvLabelName)
        val edtNameLayout = setFadeViewAnimation(binding?.edtNameLayout)
        val tvLabelEmail = setFadeViewAnimation(binding?.tvLabelEmail)
        val edtEmailLayout = setFadeViewAnimation(binding?.edtEmailLayout)
        val tvLabelPassword = setFadeViewAnimation(binding?.tvLabelPassword)
        val edtPasswordLayout = setFadeViewAnimation(binding?.edtPasswordLayout)
        val btnRegister = setFadeViewAnimation(binding?.btnRegister)
        val clContainerButton = setFadeViewAnimation(binding?.clContainerButton)

        AnimatorSet().apply {
            playSequentially(
                ivIllustrationRegister, tvRegisterTitle, tvMessage, tvLabelName,
                edtNameLayout, tvLabelEmail, edtEmailLayout, tvLabelPassword,
                edtPasswordLayout, btnRegister, clContainerButton
            )
            startDelay = 300
        }.start()
    }
}