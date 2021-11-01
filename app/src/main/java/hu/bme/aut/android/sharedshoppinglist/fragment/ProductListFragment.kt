package hu.bme.aut.android.sharedshoppinglist.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import hu.bme.aut.android.sharedshoppinglist.MainActivity
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.adapter.ProductAdapter
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentProductListBinding
import hu.bme.aut.android.sharedshoppinglist.model.Product
import hu.bme.aut.android.sharedshoppinglist.util.*
import java.time.LocalDateTime
import kotlin.random.Random

class ProductListFragment : Fragment(), ProductAdapter.ProductListener,
    ProductAdapter.OnInsertListener {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    private val args: ProductListFragmentArgs by navArgs()

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

        adapter = ProductAdapter(requireContext())
        binding.rvProducts.layoutManager = LinearLayoutManager(activity)
        binding.rvProducts.adapter = adapter
        adapter.setProducts(getProducts())
        adapter.productListener = this
        adapter.onInsertListener = this

        val swipeGesture = ProductSwipeGesture(
            context = requireContext(),
            leftSwipe = { position ->
                // TODO DELETE with server, if successful, show snackbar with undo button
                // TODO Pass user id
                adapter.deleteProductByPosition(position, Unit)
                Log.i("PRODUCT_A", "SWIPE LEFT")
            },
            rightSwipe = { position ->
                // TODO Implement pass in current user id
                adapter.purchaseProductByPosition(position, Unit)
                Log.i("PRODUCT_A", "SWIPE RIGHT")
            }
        )

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.rvProducts)

        binding.favAddProduct.setOnClickListener {
            showNewProductDialog()

            Log.i("PRODUCT_A", "ADD NEW")
        }

        //TODO API hívás, ha sikertelen snackbar üzenet.
        binding.refreshLayout.setOnRefreshListener {
            adapter.clear()
            adapter.setProducts(getProducts())
            Thread.sleep(1_000)
            binding.refreshLayout.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_products, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_export -> {
            // TODO viewmodel save
            Log.i("PRODUCT_A", "EXPORT")
            val action = ProductListFragmentDirections.actionProductListFragmentToExportFragment(
                shoppingListName = args.shoppingListName,
                shoppingListId = args.shoppingListId
            )
            findNavController().navigate(action)
            true
        }
        R.id.action_view_members -> {
            // TODO viewmodel save
            Log.i("PRODUCT_A", "MEMBERS")
            val action = ProductListFragmentDirections.actionProductListFragmentToMemberListFragment(
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

    private fun getProducts(): List<Product> {
        return listOf(
            Product(
                Random.nextLong(1000), Random.nextLong(1000),
                "Name ${Random.nextLong(1000)}", Random.nextBoolean(),
                Random.nextLong(1000), Random.nextLong(1000),
                LocalDateTime.now().minusHours(Random.nextLong(168)),
                LocalDateTime.now().plusHours(Random.nextLong(168)),
                1, null
            ),
            Product(
                Random.nextLong(1000), Random.nextLong(1000),
                "Name ${Random.nextLong(1000)}", Random.nextBoolean(),
                Random.nextLong(1000), Random.nextLong(1000),
                LocalDateTime.now().minusHours(Random.nextLong(168)),
                LocalDateTime.now().plusHours(Random.nextLong(168)),
                1, null
            ),
        )
    }

    override fun scrollToTop() {
        binding.rvProducts.smoothScrollToPosition(0)
    }

    // TODO
    override fun onItemLongClick(product: Product) {
        // TODO CHANGE USER ID
        if (product.AddedByID != 2L) {
            showSnackBarFromAdapter(R.string.you_cant_edit)
        }

        // TODO SHOW MODAL, SHAVE CHANGE, UPDATE VIEW IF SUCCESSFUL

        Log.i("PRODUCT_A", "LONG CLICK ${product.Name}")
        product.let {
            Log.i("PRODUCT_A", "${product.Name} PURCHASED BY ${product.BoughtByID}")
        }
    }

    // TODO Actually delete, if error, return false
    override fun onItemDelete(product: Product, position: Int): Boolean {
        binding.root.showSnackBar(
            title = R.string.shopping_list_deleted,
            anchor = binding.favAddProduct,
            actionText = R.string.action_undo,
            action = {
                adapter.addProduct(product, position)
            }
        )

        Log.i("PRODUCT_A", "DELETED ${product.Name}")
        return true
    }

    override fun onItemPurchased(product: Product): Boolean {
        // TODO SEND PURCHASE TO SERVER
        return true
    }

    override fun onItemPurchasedUndo(product: Product): Boolean {
        // TODO SEND UNDO PURCHASE TO SERVER
        return true
    }

    override fun showSnackBarFromAdapter(resourceString: Int) {
        binding.root.showSnackBar(resourceString)
    }

    private fun showNewProductDialog() {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.alert_dialog_add_product))
            .setView(R.layout.dialog_new_product)
            .setPositiveButton(getString(R.string.alertdialog_add)) { _, _ -> }
            .setNegativeButton(getString(R.string.alert_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialogBuilder.show()

        dialogBuilder.setPositiveButtonWithValidation {
            val etProductName =
                (dialogBuilder as? AlertDialog)?.findViewById<TextInputLayout>(R.id.etProductName)
            etProductName?.editText?.doAfterTextChanged {
                etProductName.clearErrorIfRequiredValid(requireActivity())
            }
            if (!etProductName!!.checkAndShowIfRequiredFilled(requireActivity())) {
                return@setPositiveButtonWithValidation
            }

            val etProductPrice =
                (dialogBuilder as? AlertDialog)?.findViewById<TextInputLayout>(R.id.etProductPrice)
            etProductPrice?.editText?.doAfterTextChanged {
                etProductPrice.clearErrorIfRequiredValid(requireActivity())
            }
            if (!etProductPrice!!.checkAndShowIfRequiredFilled(requireActivity())) {
                return@setPositiveButtonWithValidation
            }

            val smSharedItem =
                (dialogBuilder as? AlertDialog)?.findViewById<SwitchMaterial>(R.id.smSharedItem)

            val name = etProductName.editText?.text.toString()
            val price = etProductPrice.editText?.text.toString()
            val isShared = !smSharedItem?.isSelected!!


            val separatorPosition = price.indexOf('.')

            // todo check if price contains dot or not
            val priceAsLong = (price.substring(0, separatorPosition) + price.substring(
                separatorPosition + 1,
                separatorPosition + 3
            )).toLong()

            // TODO Save with database, change data
            val product = Product(
                Random.nextLong(1000), Random.nextLong(1000),
                name, isShared,
                priceAsLong, priceAsLong,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(Random.nextLong(168)),
                1, null
            )

            adapter.addProduct(product)


            Log.i("SL_A", "created product: $name")

            dialogBuilder.dismiss()
        }

    }
}