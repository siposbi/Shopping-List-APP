package hu.bme.aut.android.sharedshoppinglist.fragment

import android.content.*
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.ShoppingListApplication
import hu.bme.aut.android.sharedshoppinglist.adapter.ShoppingListAdapter
import hu.bme.aut.android.sharedshoppinglist.database.ShoppingListDao
import hu.bme.aut.android.sharedshoppinglist.databinding.DialogInputShoppingListCreateBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.DialogInputShoppingListJoinCodeBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.DialogInputShoppingListRenameBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentShoppingListBinding
import hu.bme.aut.android.sharedshoppinglist.model.ShoppingList
import hu.bme.aut.android.sharedshoppinglist.network.SessionManager
import hu.bme.aut.android.sharedshoppinglist.network.ShoppingListClient
import hu.bme.aut.android.sharedshoppinglist.util.*
import kotlinx.coroutines.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ShoppingListFragment : Fragment(), ShoppingListAdapter.ShoppingListCardListener,
    CoroutineScope by MainScope() {
    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var database: ShoppingListDao
    private lateinit var apiClient: ShoppingListClient
    private var lastDeletedShareCode: String? = null
    private var lastDeletedPosition: Int? = null

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

        adapter = ShoppingListAdapter(this)
        binding.rvShoppingLists.layoutManager = LinearLayoutManager(activity)
        binding.rvShoppingLists.adapter = adapter

        loadShoppingLists()
        binding.rlShoppingLists.setOnRefreshListener { reloadShoppingLists() }

        initFab()
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

    private fun loadShoppingLists() {
        apiClient.getShoppingLists(onSuccess = ::onListsLoaded, onError = ::onListLoadFailed)
    }

    private fun reloadShoppingLists() {
        binding.rlShoppingLists.isRefreshing = true
        loadShoppingLists()
    }

    private fun showFabPrompt() {
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(binding.fabExpandable)
            .setPrimaryText(R.string.fab_prompt_no_lists_primary)
            .setSecondaryText(R.string.fab_prompt_no_lists_secondary)
            .setAppColors(requireContext())
            .show()
    }

    private fun onListsLoaded(shoppingLists: List<ShoppingList>) {
        if (shoppingLists.isEmpty())
            showFabPrompt()
        binding.rlShoppingLists.isRefreshing = false
        binding.loadingIndicator.visibility = View.GONE
        adapter.setShoppingLists(shoppingLists)
    }

    private fun onListLoadFailed(error: String) {
        binding.rlShoppingLists.isRefreshing = false
        binding.loadingIndicator.visibility = View.GONE
        showSnackBar(error, anchor = binding.fabExpandable)
    }

    private fun initFab() {
        binding.fabExpandable.efabIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionJoin.fabOptionIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionCreate.fabOptionIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionCreate.setOnClickListener { showCreateDialog() }
        binding.fabOptionJoin.setOnClickListener { showJoinDialog() }
    }

    // TODO check if items loaded from network match this, if not show dialog
    private fun loadItemsInBackground() = launch {
        val items = withContext(Dispatchers.IO) {
            database.getAllShoppingLists()
        }
    }

    private fun showJoinDialog() {
        val dialogBinding = DialogInputShoppingListJoinCodeBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.alert_dialog_join_shopping_list_title)
            .setView(dialogBinding.root)
            .setPositiveButtonText(R.string.alert_dialog_ok)
            .setDismissButton(R.string.alert_dialog_cancel)
            .create()
            .setPositiveButtonOnShow { dialog ->
                val etJoinCode = dialogBinding.etJoinCode
                if (!etJoinCode.requiredValid(requireContext()))
                    return@setPositiveButtonOnShow

                val shareCode = etJoinCode.text

                apiClient.join(
                    shareCode = shareCode,
                    onSuccess = ::onListJoined,
                    onError = ::requestFailed
                )

                dialog.dismiss()
            }.show()
    }

    private fun onListJoined(shoppingList: ShoppingList) {
        adapter.addShoppingList(shoppingList)
    }

    private fun showCreateDialog() {
        val dialogBinding = DialogInputShoppingListCreateBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.alert_dialog_create_shopping_list_title)
            .setView(dialogBinding.root)
            .setPositiveButtonText(R.string.alert_dialog_create)
            .setDismissButton(R.string.alert_dialog_cancel)
            .create()
            .setPositiveButtonOnShow { dialog ->
                val etShoppingListName = dialogBinding.etShoppingListName
                if (!etShoppingListName.requiredAndLengthValid(requireContext(), 20))
                    return@setPositiveButtonOnShow

                apiClient.create(
                    name = etShoppingListName.text,
                    onSuccess = ::onListCreated,
                    onError = ::requestFailed
                )

                dialog.dismiss()
            }.show()
    }

    private fun onListCreated(shoppingList: ShoppingList) {
        adapter.addShoppingList(shoppingList)
    }

    override fun onItemClick(shoppingList: ShoppingList) {
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

        showSnackBar(R.string.share_code_copied, anchor = binding.fabExpandable)
    }

    override fun onDeleteClick(shoppingList: ShoppingList, position: Int) {
        lastDeletedShareCode = shoppingList.shareCode
        lastDeletedPosition = position
        apiClient.leave(
            listId = shoppingList.id,
            onSuccess = ::onListLeft,
            onError = ::requestFailed
        )
    }

    private fun onListLeft(shoppingListId: Long) {
        adapter.deleteShoppingList(shoppingListId)
        showSnackBar(
            title = R.string.shopping_list_deleted,
            actionText = R.string.action_undo,
            action = {
                lastDeletedShareCode?.let {
                    apiClient.join(
                        shareCode = it,
                        onSuccess = ::onListLeftUndo,
                        onError = ::requestFailed
                    )
                }
            },
            anchor = binding.fabExpandable
        )
    }

    private fun onListLeftUndo(shoppingList: ShoppingList) {
        lastDeletedPosition?.let { adapter.addShoppingList(shoppingList, it) }
    }

    override fun onItemLongClick(shoppingList: ShoppingList) {
        val dialogBinding = DialogInputShoppingListRenameBinding.inflate(layoutInflater)
        dialogBinding.etShoppingListRename.text = shoppingList.name
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.alert_dialog_rename_title, shoppingList.name))
            .setView(dialogBinding.root)
            .setPositiveButtonText(R.string.alert_dialog_rename)
            .setDismissButton(R.string.alert_dialog_cancel)
            .create()
            .setPositiveButtonOnShow { dialog ->
                val etShoppingListRename = dialogBinding.etShoppingListRename
                if (!etShoppingListRename.requiredAndLengthValid(requireActivity(), 20))
                    return@setPositiveButtonOnShow
                val newName = etShoppingListRename.text
                if (newName == shoppingList.name) {
                    dialog.dismiss()
                    return@setPositiveButtonOnShow
                }

                apiClient.rename(
                    listId = shoppingList.id,
                    newName = newName,
                    onSuccess = ::onListRenamed,
                    onError = ::requestFailed
                )

                dialog.dismiss()
            }.show()
    }

    private fun onListRenamed(shoppingList: ShoppingList) {
        adapter.updateShoppingList(shoppingList)
    }

    private fun requestFailed(error: String) {
        showSnackBar(error, anchor = binding.fabExpandable)
    }

    override fun scrollToTop() {
        binding.rvShoppingLists.smoothScrollToPosition(0)
    }
}