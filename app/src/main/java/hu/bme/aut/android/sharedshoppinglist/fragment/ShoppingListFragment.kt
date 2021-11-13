package hu.bme.aut.android.sharedshoppinglist.fragment

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.ShoppingListApplication
import hu.bme.aut.android.sharedshoppinglist.adapter.ShoppingListAdapter
import hu.bme.aut.android.sharedshoppinglist.database.ShoppingListDao
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentShoppingListBinding
import hu.bme.aut.android.sharedshoppinglist.model.ShoppingList
import hu.bme.aut.android.sharedshoppinglist.network.SessionManager
import hu.bme.aut.android.sharedshoppinglist.network.ShoppingListClient
import hu.bme.aut.android.sharedshoppinglist.util.*
import kotlinx.coroutines.*
import java.time.LocalDateTime
import kotlin.random.Random

// TODO lista nevének hosszát ellenőrizni
class ShoppingListFragment : Fragment(), ShoppingListAdapter.ShoppingListItemCardListener,
    CoroutineScope by MainScope() {
    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var database: ShoppingListDao
    private lateinit var apiClient: ShoppingListClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        database = ShoppingListApplication.shoppingListDatabase.shoppingListDao()
        apiClient = ShoppingListClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        setHasOptionsMenu(true)

        adapter = ShoppingListAdapter()
        binding.rvShoppingLists.layoutManager = LinearLayoutManager(activity)
        binding.rvShoppingLists.adapter = adapter
        adapter.itemCardListener = this
        loadShoppingLists()

        binding.rlShoppingLists.setOnRefreshListener { loadShoppingLists() }

        initFab()
    }

    private fun loadShoppingLists() {
        binding.rlShoppingLists.isRefreshing = true
        apiClient.getShoppingLists(onSuccess = ::onListsLoaded, onError = ::onListLoadFailed)
    }

    private fun onListsLoaded(shoppingLists: List<ShoppingList>) {
        binding.rlShoppingLists.isRefreshing = false
        adapter.setShoppingLists(shoppingLists)
    }

    private fun onListLoadFailed(error: String) {
        binding.rlShoppingLists.isRefreshing = false
        showSnackBar(error)
    }

    private fun initFab() {
        binding.fabExpandable.efabIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionJoin.fabOptionIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionCreate.fabOptionIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionCreate.setOnClickListener { showCreateDialog() }
        binding.fabOptionJoin.setOnClickListener { showJoinDialog() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_logout, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {
            SessionManager(requireContext()).logoutUser()
            val action = ShoppingListFragmentDirections.actionShoppingListFragmentToLoginFragment()
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

    // TODO check if items loaded from network match this, if not show dialog
    private fun loadItemsInBackground() = launch {
        val items = withContext(Dispatchers.IO) {
            database.getAllShoppingLists()
        }
    }

    private fun showJoinDialog() {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.alert_dialog_join_shopping_list_title))
            .setView(R.layout.dialog_input_shopping_list_join_code)
            .setPositiveButton(getString(R.string.alert_dialog_ok)) { _, _ -> }
            .setNegativeButton(getString(R.string.alert_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }.create()

        dialogBuilder.show()

        dialogBuilder.setPositiveButtonWithValidation {
            val textInputLayout =
                (dialogBuilder as? AlertDialog)?.findViewById<TextInputLayout>(R.id.etJoinCode)
            textInputLayout?.editText?.doAfterTextChanged {
                textInputLayout.clearErrorIfRequiredValid(requireActivity())
            }
            if (!textInputLayout!!.checkAndShowIfRequiredFilled(requireActivity())) {
                return@setPositiveButtonWithValidation
            }
            val shareCode = textInputLayout.editText?.text?.toString()

            // ACTUALLY JOIN

            Log.i("SL_A", "INPUT SHARE CODE: $shareCode")
            dialogBuilder.dismiss()
        }
    }

    private fun showCreateDialog() {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.alert_dialog_create_shopping_list_title))
            .setView(R.layout.dialog_input_shopping_list_create)
            .setPositiveButton(getString(R.string.alert_dialog_create)) { _, _ -> }
            .setNegativeButton(getString(R.string.alert_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialogBuilder.show()

        dialogBuilder.setPositiveButtonWithValidation {
            val textInputLayout =
                (dialogBuilder as? AlertDialog)?.findViewById<TextInputLayout>(R.id.etShoppingListName)
            textInputLayout?.editText?.doAfterTextChanged {
                textInputLayout.clearErrorIfRequiredValid(requireActivity())
            }
            if (!textInputLayout!!.checkAndShowIfRequiredFilled(requireActivity())) {
                return@setPositiveButtonWithValidation
            }
            val name = textInputLayout.editText?.text?.toString()

            // TODO Save with database

            val id = Random.nextLong(1000)
            adapter.addShoppingList(
                ShoppingList(
                    id, name!!, 0, "SC$id",
                    LocalDateTime.now().minusHours(Random.nextLong(168)),
                    LocalDateTime.now().plusHours(Random.nextLong(168)),
                    Random.nextBoolean()
                )
            )


            Log.i("SL_A", "INPUT LIST NAME: $name")

            dialogBuilder.dismiss()
        }
    }

    override fun onItemClick(shoppingList: ShoppingList) {
        Log.i("SL_A", "CLICK ${shoppingList.id}")
        val action = ShoppingListFragmentDirections.actionShoppingListFragmentToProductListFragment(
            shoppingListName = shoppingList.name,
            shoppingListId = shoppingList.id
        )
        findNavController().navigate(action)
    }

    override fun onShareClick(shoppingList: ShoppingList) {
        val clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Shopping List join code", shoppingList.shareCode)
        clipboardManager.setPrimaryClip(clipData)

        Snackbar.make(binding.root, R.string.share_code_copied, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.fabExpandable).show()

        Log.i("SL_A", "SHARE ${shoppingList.id}")
    }

    override fun onDeleteClick(shoppingList: ShoppingList, position: Int) {
        // TODO Delete item, if successfull, delete from list
        adapter.deleteShoppingList(shoppingList)

        Snackbar.make(binding.root, R.string.shopping_list_deleted, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.fabExpandable)
            .setAction(R.string.action_undo) {
                // TODO cancel delete via server, is successfull, continue
                adapter.addShoppingList(shoppingList, position)
            }.show()

        Log.i("SL_A", "DELETE ${shoppingList.id}")
    }

    override fun onItemLongClick(shoppingList: ShoppingList, index: Int) {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(
                getString(
                    R.string.alert_dialog_rename_shopping_list_title,
                    shoppingList.name
                )
            )
            .setView(R.layout.dialog_input_shopping_list_rename)
            .setPositiveButton(getString(R.string.alert_dialog_rename)) { _, _ -> }
            .setNegativeButton(getString(R.string.alert_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialogBuilder.show()

        (dialogBuilder as? AlertDialog)?.findViewById<TextInputLayout>(R.id.etShoppingListRename)?.editText?.setText(
            shoppingList.name
        )

        dialogBuilder.setPositiveButtonWithValidation {
            val textInputLayout =
                (dialogBuilder as? AlertDialog)?.findViewById<TextInputLayout>(R.id.etShoppingListRename)!!
            textInputLayout.editText?.doAfterTextChanged {
                textInputLayout.clearErrorIfRequiredValid(requireActivity())
                textInputLayout.clearErrorIfLengthValid(requireContext(), 20)
            }
            if (!textInputLayout.checkAndShowIfRequiredFilled(requireActivity())) {
                return@setPositiveButtonWithValidation
            }
            if (!textInputLayout.checkAndShowIfLengthValid(requireActivity(), 20)) {
                return@setPositiveButtonWithValidation
            }
            val newName = textInputLayout.editText?.text?.toString()
            if (newName == shoppingList.name) {
                dialogBuilder.dismiss()
                return@setPositiveButtonWithValidation
            }

            // TODO Save with database

//            shoppingList.name = newName!!
            adapter.updateShoppingListWithIndex(shoppingList, index)


            Log.i("SL_A", "INPUT LIST NAME: $newName")

            dialogBuilder.dismiss()
        }
    }

    override fun scrollToTop() {
        binding.rvShoppingLists.smoothScrollToPosition(0)
    }
}
