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
import hu.bme.aut.android.sharedshoppinglist.network.RegisterModel
import hu.bme.aut.android.sharedshoppinglist.network.ShoppingListClient
import hu.bme.aut.android.sharedshoppinglist.util.requiredValid
import hu.bme.aut.android.sharedshoppinglist.util.showSnackBar

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
        binding.etEmail.editText?.setText(args.email)
        binding.etPassword.editText?.setText(args.password)

        binding.btnRegister.setOnClickListener {
            if (!binding.etFirstName.requiredValid(requireActivity()) or
                !binding.etLastName.requiredValid(requireActivity()) or
                !binding.etEmail.requiredValid(requireActivity()) or
                !binding.etPassword.requiredValid(requireActivity())
            ) {
                return@setOnClickListener
            }
            apiClient.register(
                registerModel = RegisterModel(
                    firstName = binding.etFirstName.editText?.text.toString(),
                    lastName = binding.etLastName.editText?.text.toString(),
                    email = binding.etEmail.editText?.text.toString(),
                    password =binding.etPassword.editText?.text.toString()
                ),
                onSuccess = ::successfulRegistration,
                onError = ::failedRegistration
            )
        }

        binding.etPassword.editText?.doAfterTextChanged {
            if (binding.etPassword.error != null && checkIfPasswordIsValidAndShow(binding.etPassword)) {
                binding.etPassword.error = null
            }
        }
    }

    private fun checkIfPasswordIsValidAndShow(textInput: TextInputLayout): Boolean {
        if (binding.etPassword.editText!!.text.toString().length >= 8) {
            return true
        }
        textInput.error = getString(R.string.password_error)
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