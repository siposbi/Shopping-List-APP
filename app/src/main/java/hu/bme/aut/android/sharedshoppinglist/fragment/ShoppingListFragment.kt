package hu.bme.aut.android.sharedshoppinglist.fragment

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.ShoppingListApplication
import hu.bme.aut.android.sharedshoppinglist.adapter.ShoppingListAdapter
import hu.bme.aut.android.sharedshoppinglist.database.ChangeLog
import hu.bme.aut.android.sharedshoppinglist.database.RoomShoppingList
import hu.bme.aut.android.sharedshoppinglist.databinding.DialogInputShoppingListCreateBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.DialogInputShoppingListJoinCodeBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.DialogInputShoppingListRenameBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentShoppingListBinding
import hu.bme.aut.android.sharedshoppinglist.model.ShoppingList
import hu.bme.aut.android.sharedshoppinglist.util.*
import kotlinx.coroutines.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ShoppingListFragment : Fragment(), ShoppingListAdapter.ShoppingListCardListener,
    CoroutineScope by MainScope() {
    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ShoppingListAdapter
    private val apiClient = ShoppingListApplication.apiClient
    private val database = ShoppingListApplication.shoppingListDatabase.shoppingListDao()
    private var lastDeletedShareCode: String? = null
    private var lastDeletedPosition: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        setHasOptionsMenu(true)

        adapter = ShoppingListAdapter(this, requireContext())
        binding.recyclerView.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.recyclerView.adapter = adapter

        loadShoppingLists()
        binding.recyclerView.refreshLayout.setOnRefreshListener { reloadShoppingLists() }
        binding.recyclerView.setOnRetryClickListener { loadShoppingLists() }

        initFab()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_logout, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {
            ShoppingListApplication.sessionManager.logoutUser()
            val action = ShoppingListFragmentDirections.actionShoppingListFragmentToLoginFragment()
            findNavController().navigate(action)
            true
        }
        R.id.action_user_info -> {
            showSnackBar(
                requireContext().getString(
                    R.string.logged_in_email,
                    ShoppingListApplication.sessionManager.getUserEmail()!!
                ),
                anchor = binding.fabExpandable
            )
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
        binding.recyclerView.showLoadingView()
        apiClient.shoppingListGetListsForUser(
            onSuccess = ::onListsLoaded,
            onError = ::onListLoadFailed
        )
    }

    private fun reloadShoppingLists() {
        apiClient.shoppingListGetListsForUser(
            onSuccess = ::onListsLoaded,
            onError = ::onListReloadFailed
        )
    }

    private fun onListsLoaded(shoppingLists: List<ShoppingList>) {
        adapter.setShoppingLists(shoppingLists)
        loadItemsInBackground(shoppingLists)
    }

    private fun onListLoadFailed(error: String) {
        binding.recyclerView.showErrorView()
        showSnackBar(error, anchor = binding.fabExpandable)
    }

    private fun onListReloadFailed(error: String) {
        binding.recyclerView.hideAllViews()
        showSnackBar(error, anchor = binding.fabExpandable)
    }

    private fun showFabPrompt() {
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(binding.fabExpandable)
            .setPrimaryText(R.string.fab_prompt_no_lists_primary)
            .setSecondaryText(R.string.fab_prompt_no_lists_secondary)
            .setAppColors(requireContext())
            .show()
    }

    private fun initFab() {
        binding.fabExpandable.efabIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionJoin.fabOptionIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionCreate.fabOptionIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionCreate.setOnClickListener { showCreateDialog() }
        binding.fabOptionJoin.setOnClickListener { showJoinDialog() }
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

                apiClient.shoppingListGet(
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

                apiClient.shoppingListCreate(
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
        apiClient.shoppingListLeave(
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
                    apiClient.shoppingListGet(
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

                apiClient.shoppingListRename(
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

    private fun loadItemsInBackground(nShoppingLists: List<ShoppingList>) = launch {
        withContext(Dispatchers.IO) {
            val dbShoppingLists = database.getAllShoppingLists()

            dbShoppingLists.forEach { dbSL ->
                if (nShoppingLists.none { nSL -> nSL.id == dbSL.id })
                    database.deleteShoppingListById(dbSL.id)
            }

            val changelogs = mutableListOf<ChangeLog>()
            nShoppingLists.forEach { nSl ->
                val dbList = dbShoppingLists.singleOrNull { dbSl ->
                    dbSl.id == nSl.id
                }
                if (dbList == null){
                    database.insertShoppingList(RoomShoppingList(nSl.id, nSl.numberOfProducts))
                    return@forEach
                }
                if (nSl.numberOfProducts != dbList.numberOfProducts)
                    changelogs.add(ChangeLog(nSl.name, dbList.numberOfProducts, nSl.numberOfProducts))
                database.updateShoppingList(dbList.id, nSl.numberOfProducts)
            }

            showChangesDialog(changelogs)
        }
    }

    private fun showChangesDialog(changelogs: MutableList<ChangeLog>) {
        for (cl in changelogs)
            Log.i("CHANGE_LIST", cl.toString())
    }

    private fun requestFailed(error: String) {
        showSnackBar(error, anchor = binding.fabExpandable)
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