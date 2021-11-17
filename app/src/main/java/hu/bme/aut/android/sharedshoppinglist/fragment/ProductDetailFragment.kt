package hu.bme.aut.android.sharedshoppinglist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.sharedshoppinglist.ShoppingListApplication
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentProductDetailBinding
import hu.bme.aut.android.sharedshoppinglist.model.Product
import hu.bme.aut.android.sharedshoppinglist.util.showSnackBar

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ProductDetailFragmentArgs by navArgs()
    private val apiClient = ShoppingListApplication.apiClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        loadProduct()
        binding.errorView.retryButton.setOnClickListener { loadProduct() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadProduct() {
        binding.loadingView.root.visibility = View.VISIBLE
        apiClient.productGet(
            productId = args.productId,
            onSuccess = ::onProductLoaded,
            onError = ::onProductLoadFailed
        )
    }

    private fun onProductLoaded(product: Product) {
        binding.loadingView.root.visibility = View.GONE
        // TODO Set view from data
    }

    private fun onProductLoadFailed(error: String) {
        binding.loadingView.root.visibility = View.GONE
        binding.errorView.root.visibility = View.VISIBLE
        showSnackBar(error, length = Snackbar.LENGTH_LONG)
    }
}