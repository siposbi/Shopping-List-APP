package hu.bme.aut.android.sharedshoppinglist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentLoginBinding
import hu.bme.aut.android.sharedshoppinglist.util.setUserLoggedIn

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        binding.btnLogin.setOnClickListener {
            // TODO Login via backend.
            requireActivity().setUserLoggedIn(true)
            val action = LoginFragmentDirections.actionLoginFragmentToShoppingListFragment()
            findNavController().navigate(action)
        }

        binding.btnRegister.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment(
                email = binding.etEmail.editText?.text.toString(),
                password = binding.etPassword.editText?.text.toString()
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}