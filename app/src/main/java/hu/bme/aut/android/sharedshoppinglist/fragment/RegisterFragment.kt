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
import hu.bme.aut.android.sharedshoppinglist.util.checkAndShowIfRequiredFilled
import hu.bme.aut.android.sharedshoppinglist.util.clearErrorIfRequiredValid

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val args: RegisterFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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

    private fun initForm(){
        binding.etEmail.editText?.setText(args.email)
        binding.etPassword.editText?.setText(args.password)

        binding.btnRegister.setOnClickListener {
            if (!binding.etFirstName.checkAndShowIfRequiredFilled(requireActivity()) or
                !binding.etLastName.checkAndShowIfRequiredFilled(requireActivity()) or
                !binding.etEmail.checkAndShowIfRequiredFilled(requireActivity()) or
                !binding.etPassword.checkAndShowIfRequiredFilled(requireActivity())
            ) {
                return@setOnClickListener
            }

            // TODO Register via backend.

            findNavController().navigateUp()
        }

        binding.etPassword.editText?.doAfterTextChanged {
            if (binding.etPassword.error != null && checkIfPasswordIsValidAndShow(binding.etPassword)) {
                binding.etPassword.error = null
            }
        }

        binding.etFirstName.editText?.doAfterTextChanged {
            binding.etFirstName.clearErrorIfRequiredValid(requireActivity())
        }
        binding.etLastName.editText?.doAfterTextChanged {
            binding.etLastName.clearErrorIfRequiredValid(requireActivity())
        }
        binding.etEmail.editText?.doAfterTextChanged {
            binding.etEmail.clearErrorIfRequiredValid(requireActivity())
        }
    }

    private fun checkIfPasswordIsValidAndShow(textInput: TextInputLayout): Boolean {
        if (binding.etPassword.editText!!.text.toString().length >= 8) {
            return true
        }
        textInput.error = getString(R.string.password_error)
        return false
    }

}