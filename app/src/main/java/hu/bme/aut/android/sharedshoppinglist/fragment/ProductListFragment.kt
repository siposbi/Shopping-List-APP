package hu.bme.aut.android.sharedshoppinglist.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.ShoppingListApplication
import hu.bme.aut.android.sharedshoppinglist.adapter.ProductAdapter
import hu.bme.aut.android.sharedshoppinglist.databinding.DialogBuyProductBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.DialogNewProductBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentProductListBinding
import hu.bme.aut.android.sharedshoppinglist.model.ProductMinimal
import hu.bme.aut.android.sharedshoppinglist.network.model.ProductCreateModel
import hu.bme.aut.android.sharedshoppinglist.network.model.ProductUpdateModel
import hu.bme.aut.android.sharedshoppinglist.util.*
import kotlinx.coroutines.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ProductListFragment : Fragment(), ProductAdapter.ProductListener,
    CoroutineScope by MainScope() {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    private val args: ProductListFragmentArgs by navArgs()
    private val apiClient = ShoppingListApplication.apiClient
    private val database = ShoppingListApplication.shoppingListDatabase.shoppingListDao()
    private var lastInteractedProductId: Long? = null
    private var lastInteractedProductPosition: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        setHasOptionsMenu(true)

        adapter = ProductAdapter(this, requireContext())
        binding.recyclerView.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.recyclerView.adapter = adapter

        initSwipeGesture()
        loadProducts()
        binding.recyclerView.setOnRetryClickListener { loadProducts() }
        binding.fabAddProduct.setOnClickListener { showNewProductDialog() }
        binding.recyclerView.refreshLayout.setOnRefreshListener { reloadProducts() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_products, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_export -> {
            val action = ProductListFragmentDirections.actionProductListFragmentToExportFragment(
                shoppingListName = args.shoppingListName,
                shoppingListId = args.shoppingListId
            )
            findNavController().navigate(action)
            true
        }
        R.id.action_view_members -> {
            val action =
                ProductListFragmentDirections.actionProductListFragmentToMemberListFragment(
                    shoppingListName = args.shoppingListName,
                    shoppingListId = args.shoppingListId
                )
            findNavController().navigate(action)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initSwipeGesture() {
        val swipeGesture = ProductSwipeGesture(
            context = requireContext(),
            leftSwipe = { position ->
                val product = adapter.getProductByAdapterPosition(position)
                lastInteractedProductId = product.id
                lastInteractedProductPosition = position
                apiClient.productDelete(
                    productId = product.id,
                    onSuccess = ::onProductDeleted,
                    onError = ::swipeRequestFailed
                )
            },
            rightSwipe = { position ->
                val product = adapter.getProductByAdapterPosition(position)
                lastInteractedProductId = product.id
                lastInteractedProductPosition = position
                if (product.isBought) {
                    apiClient.productUndoBuy(
                        productId = product.id,
                        onSuccess = ::onProductBoughtUndo,
                        onError = ::swipeRequestFailed
                    )
                } else {
                    purchaseProduct(product)
                }
            }
        )

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.recyclerView.recyclerView)
    }

    private fun onProductDeleted(productId: Long) {
        adapter.deleteProduct(productId)
        showSnackBar(
            title = R.string.product_deleted,
            actionText = R.string.action_undo,
            action = {
                lastInteractedProductId?.let {
                    apiClient.productUndoDelete(
                        productId = it,
                        onSuccess = ::onProductDeleteUndo,
                        onError = ::requestFailed
                    )
                }
            },
            anchor = binding.fabAddProduct
        )
        deleteFromDb()
    }

    private fun onProductDeleteUndo(product: ProductMinimal) {
        lastInteractedProductPosition?.let { adapter.addProduct(product, it) }
        addToDb()
    }

    private fun onProductBought(product: ProductMinimal) {
        adapter.updateProduct(product)
        showSnackBar(
            title = R.string.product_bought,
            actionText = R.string.action_undo,
            action = {
                lastInteractedProductId?.let {
                    apiClient.productUndoBuy(
                        productId = it,
                        onSuccess = ::onProductBoughtUndo,
                        onError = ::requestFailed
                    )
                }
            },
            anchor = binding.fabAddProduct
        )
    }

    private fun onProductBoughtUndo(product: ProductMinimal) {
        adapter.updateProduct(product)
    }

    private fun loadProducts() {
        binding.recyclerView.showLoadingView()
        apiClient.productGetAllOfList(
            listId = args.shoppingListId,
            onSuccess = ::onProductsLoaded,
            onError = ::onProductLoadFailed
        )
    }

    private fun reloadProducts() {
        apiClient.productGetAllOfList(
            listId = args.shoppingListId,
            onSuccess = ::onProductsLoaded,
            onError = ::onProductReloadFailed
        )
    }

    private fun onProductsLoaded(products: List<ProductMinimal>) {
        adapter.setProducts(products)
    }

    private fun onProductLoadFailed(error: String) {
        binding.recyclerView.showErrorView()
        showSnackBar(error, anchor = binding.fabAddProduct)
    }

    private fun onProductReloadFailed(error: String) {
        binding.recyclerView.hideAllViews()
        showSnackBar(error, anchor = binding.fabAddProduct)
    }

    private fun showFabPrompt() {
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(binding.fabAddProduct)
            .setPrimaryText(R.string.fab_prompt_no_products_primary)
            .setSecondaryText(R.string.fab_prompt_no_products_secondary)
            .setAppColors(requireContext())
            .show()
    }

    override fun onItemClick(product: ProductMinimal) {
        val action = ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(
            productId = product.id,
            productName = product.name
        )
        findNavController().navigate(action)
    }

    override fun onItemLongClick(product: ProductMinimal) {
        val dialogBinding = DialogNewProductBinding.inflate(layoutInflater)
        dialogBinding.etProductName.text = product.name
        dialogBinding.etProductPrice.text =
            product.price.getPriceAsStringWithoutSign(requireContext())
        dialogBinding.smSharedItem.isChecked = product.isShared
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.alert_dialog_create_product_title)
            .setView(dialogBinding.root)
            .setPositiveButtonText(R.string.alert_dialog_edit)
            .setDismissButton(R.string.alert_dialog_cancel)
            .create()
            .setPositiveButtonOnShow { dialog ->
                val etProductName = dialogBinding.etProductName
                if (!etProductName.requiredAndLengthValid(requireContext(), 30))
                    return@setPositiveButtonOnShow
                val etProductPrice = dialogBinding.etProductPrice
                if (!etProductPrice.requiredValid(requireContext()))
                    return@setPositiveButtonOnShow

                apiClient.productUpdate(
                    productId = product.id,
                    newProduct = ProductUpdateModel(
                        name = etProductName.text,
                        price = etProductPrice.getPriceAsLong(),
                        isShared = dialogBinding.smSharedItem.isChecked
                    ),
                    onSuccess = ::onProductUpdated,
                    onError = ::requestFailed
                )

                dialog.dismiss()
            }.show()
    }

    private fun onProductUpdated(product: ProductMinimal) {
        adapter.updateProduct(product)
    }

    private fun showNewProductDialog() {
        val dialogBinding = DialogNewProductBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.alert_dialog_add_product)
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.alertdialog_add)) { _, _ -> }
            .setNegativeButton(getString(R.string.alert_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .setPositiveButtonOnShow { dialog ->
                val etProductName = dialogBinding.etProductName
                if (!etProductName.requiredAndLengthValid(requireContext(), 30))
                    return@setPositiveButtonOnShow
                val etProductPrice = dialogBinding.etProductPrice
                if (!etProductPrice.requiredValid(requireContext()))
                    return@setPositiveButtonOnShow

                apiClient.productCreate(
                    newProduct = ProductCreateModel(
                        shoppingListId = args.shoppingListId,
                        name = etProductName.text,
                        price = etProductPrice.getPriceAsLong(),
                        isShared = dialogBinding.smSharedItem.isChecked
                    ),
                    onSuccess = ::onProductCreated,
                    onError = ::requestFailed
                )

                dialog.dismiss()
            }.show()
    }

    private fun onProductCreated(product: ProductMinimal) {
        adapter.addProduct(product)
        addToDb()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun purchaseProduct(product: ProductMinimal) {
        val dialogBinding = DialogBuyProductBinding.inflate(layoutInflater)
        dialogBinding.etProductPrice.text =
            product.price.getPriceAsStringWithoutSign(requireContext())
        dialogBinding.etProductPrice.editText?.setOnTouchListener { _, _ ->
            dialogBinding.etProductPrice.text = ""
            false
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(product.name)
            .setView(dialogBinding.root)
            .setPositiveButtonText(R.string.alert_dialog_purchase)
            .setNegativeButton(R.string.alert_dialog_cancel) { _, _ ->
                lastInteractedProductPosition?.let { adapter.resetSwipe(it) }
            }.setOnCancelListener {
                lastInteractedProductPosition?.let { adapter.resetSwipe(it) }
            }
            .create()
            .setPositiveButtonOnShow { dialog ->
                val etProductPrice = dialogBinding.etProductPrice
                if (!etProductPrice.requiredValid(requireContext()))
                    return@setPositiveButtonOnShow

                apiClient.productBuy(
                    productId = product.id,
                    price = dialogBinding.etProductPrice.getPriceAsLong(),
                    onSuccess = ::onProductBought,
                    onError = ::swipeRequestFailed
                )

                dialog.dismiss()
            }.show()
    }

    private fun deleteFromDb() = launch {
        withContext(Dispatchers.IO) { database.deleteProduct(args.shoppingListId) }
    }

    private fun addToDb() = launch {
        withContext(Dispatchers.IO) { database.addProduct(args.shoppingListId) }
    }

    private fun requestFailed(error: String) {
        showSnackBar(error, anchor = binding.fabAddProduct)
    }

    private fun swipeRequestFailed(error: String) {
        lastInteractedProductPosition?.let { adapter.resetSwipe(it) }
        showSnackBar(error, anchor = binding.fabAddProduct)
    }

    override fun scrollToTop() {
        binding.recyclerView.recyclerView.smoothScrollToPosition(0)
    }

    override fun itemCountCallback(count: Int) {
        when (count) {
            0 -> {
                binding.recyclerView.showEmptyView()
                showFabPrompt()
            }
            else -> binding.recyclerView.hideAllViews()
        }
    }
}