package hu.bme.aut.android.sharedshoppinglist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.sharedshoppinglist.ShoppingListApplication
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentLoginBinding
import hu.bme.aut.android.sharedshoppinglist.network.model.LoginModel
import hu.bme.aut.android.sharedshoppinglist.network.model.TokenModel
import hu.bme.aut.android.sharedshoppinglist.util.showSnackBar
import hu.bme.aut.android.sharedshoppinglist.util.text

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val apiClient = ShoppingListApplication.apiClient

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
            apiClient.login(
                loginModel = LoginModel(
                    email = binding.etEmail.text,
                    password = binding.etPassword.text
                ),
                onSuccess = ::successfulLogin,
                onError = ::failedLogin
            )
        }

        binding.btnRegister.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment(
                email = binding.etEmail.text,
                password = binding.etPassword.text
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun successfulLogin(tokenModel: TokenModel) {
        apiClient.sessionManager.loginUser(tokenModel)
        val action = LoginFragmentDirections.actionLoginFragmentToShoppingListFragment()
        findNavController().navigate(action)
    }

    private fun failedLogin(error: String) {
        showSnackBar(error)
    }
}