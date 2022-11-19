package com.saeware.storyapp.ui.login

import android.animation.AnimatorSet
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.saeware.storyapp.R
import com.saeware.storyapp.databinding.FragmentLoginBinding
import com.saeware.storyapp.ui.main.MainActivity
import com.saeware.storyapp.ui.main.MainActivity.Companion.EXTRA_TOKEN
import com.saeware.storyapp.utils.AnimationUtility.setFadeViewAnimation
import com.saeware.storyapp.utils.MessageUtility.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        init()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(getString(R.string.exit))
                        setMessage(getString(R.string.confirmation_message))
                        setPositiveButton(getString(R.string.yes)) { _, _ -> requireActivity().finish()}
                        setNegativeButton(getString(R.string.no)) { _, _ -> }
                        create()
                        show()
                    }
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        binding?.apply {
            btnToRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            btnLogin.setOnClickListener { handleLogin() }
        }
    }

    private fun handleLogin() {
        showLoading(true)

        val email = binding?.edtEmail?.text.toString().trim()
        val password = binding?.edtPassword?.text.toString().trim()

        when {
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
                    launch {
                        viewModel.userLogin(email, password).observe(viewLifecycleOwner) { result ->
                            result.onSuccess { credential ->
                                credential.loginResult?.token?.let { token ->
                                    viewModel.storeAuthToken(token)
                                    Intent(requireContext(), MainActivity::class.java).also {
                                        it.putExtra(EXTRA_TOKEN, token)
                                        startActivity(it)
                                        requireActivity().finish()
                                    }
                                }
                            }
                            result.onFailure {
                                showToast(requireContext(), getString(R.string.login_failed_message))
                                showLoading(false)
                            }
                        }
                    }
                }
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            edtEmailLayout.isEnabled = !isLoading
            edtPasswordLayout.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading
            btnToRegister.isEnabled = !isLoading

            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showInputError(editText: EditText?, message: String) {
        showLoading(false)
        editText?.error = message
        showToast(requireContext(), message)
    }

    private fun playAnimation() {
        val ivIllustrationLogin = setFadeViewAnimation(binding?.ivIllustrationLogin)
        val tvMessage = setFadeViewAnimation(binding?.tvMessage)
        val tvLabelEmail = setFadeViewAnimation(binding?.tvLabelEmail)
        val edtEmailLayout = setFadeViewAnimation(binding?.edtEmailLayout)
        val tvLabelPassword= setFadeViewAnimation(binding?.tvLabelPassword)
        val edtPasswordLayout = setFadeViewAnimation(binding?.edtPasswordLayout)
        val btnLogin = setFadeViewAnimation(binding?.btnLogin)
        val clContainerButton = setFadeViewAnimation(binding?.clContainerButton)

        AnimatorSet().apply {
            playSequentially(
                ivIllustrationLogin, tvMessage, tvLabelEmail, edtEmailLayout,
                tvLabelPassword, edtPasswordLayout, btnLogin, clContainerButton
            )
            startDelay = 300
        }.start()
    }
}