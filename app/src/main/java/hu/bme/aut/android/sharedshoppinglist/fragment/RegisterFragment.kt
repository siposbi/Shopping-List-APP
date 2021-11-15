package hu.bme.aut.android.sharedshoppinglist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentRegisterBinding
import hu.bme.aut.android.sharedshoppinglist.network.model.RegisterModel
import hu.bme.aut.android.sharedshoppinglist.network.ShoppingListClient
import hu.bme.aut.android.sharedshoppinglist.util.requiredValid
import hu.bme.aut.android.sharedshoppinglist.util.showSnackBar
import hu.bme.aut.android.sharedshoppinglist.util.text

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val args: RegisterFragmentArgs by navArgs()
    private lateinit var apiClient: ShoppingListClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        apiClient = ShoppingListClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        initForm()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initForm() {
        args.email?.let { binding.etEmail.text = it }
        args.password?.let { binding.etPassword.text = it }

        binding.btnRegister.setOnClickListener {
            if (!binding.etFirstName.requiredValid(requireActivity()) or
                !binding.etLastName.requiredValid(requireActivity()) or
                !binding.etEmail.requiredValid(requireActivity()) or
                !binding.etPassword.passwordValid()
            ) {
                return@setOnClickListener
            }
            apiClient.register(
                registerModel = RegisterModel(
                    firstName = binding.etFirstName.text,
                    lastName = binding.etLastName.text,
                    email = binding.etEmail.text,
                    password = binding.etPassword.text
                ),
                onSuccess = ::successfulRegistration,
                onError = ::failedRegistration
            )
        }
    }

    private fun TextInputLayout.passwordValid(): Boolean {
        editText?.doAfterTextChanged {
            if (checkAndShowIfPasswordValid())
                error = null
        }
        return checkAndShowIfPasswordValid()
    }

    private fun TextInputLayout.checkAndShowIfPasswordValid(): Boolean {
        if (this.text.length >= 8) {
            return true
        }
        error = getString(R.string.password_error)
        return false
    }

    private fun successfulRegistration(id: Long) {
        showSnackBar("Successfully registered")
        findNavController().navigateUp()
    }

    private fun failedRegistration(error: String) {
        showSnackBar(error)
    }

}