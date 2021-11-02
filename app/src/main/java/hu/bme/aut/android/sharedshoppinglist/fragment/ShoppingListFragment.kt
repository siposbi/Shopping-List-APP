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
import hu.bme.aut.android.sharedshoppinglist.adapter.ShoppingListAdapter
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentShoppingListBinding
import hu.bme.aut.android.sharedshoppinglist.model.ShoppingList
import hu.bme.aut.android.sharedshoppinglist.util.checkAndShowIfRequiredFilled
import hu.bme.aut.android.sharedshoppinglist.util.clearErrorIfRequiredValid
import hu.bme.aut.android.sharedshoppinglist.util.setPositiveButtonWithValidation
import hu.bme.aut.android.sharedshoppinglist.util.setUserLoggedIn
import kotlinx.android.synthetic.main.fragment_register.*
import java.time.LocalDateTime
import kotlin.random.Random

// TODO lista nevének hosszát ellenőrizni
class ShoppingListFragment : Fragment(), ShoppingListAdapter.ShoppingListItemCardListener,
    ShoppingListAdapter.OnInsertListener {
    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ShoppingListAdapter

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

        adapter = ShoppingListAdapter()
        binding.rvShoppingLists.layoutManager = LinearLayoutManager(activity)
        binding.rvShoppingLists.adapter = adapter
        adapter.setShoppingLists(getShoppingList())
        adapter.itemCardListener = this
        adapter.onInsertListener = this

        binding.fabExpandable.efabIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionJoin.fabOptionIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))
        binding.fabOptionCreate.fabOptionIcon?.setTint(requireActivity().getColor(R.color.secondaryTextColor))

        binding.fabOptionCreate.setOnClickListener {
            showCreateDialog()
            Log.i("SL_A", "FAB CREATE")

        }
        binding.fabOptionJoin.setOnClickListener {
            showJoinDialog()
            Log.i("SL_A", "FAB JOIN")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_logout, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {
            requireActivity().setUserLoggedIn(false)
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
                    id, name!!, 0,"SC$id",
                    LocalDateTime.now().minusHours(Random.nextLong(168)),
                    LocalDateTime.now().plusHours(Random.nextLong(168)),
                    Random.nextBoolean()
                )
            )


            Log.i("SL_A", "INPUT LIST NAME: $name")

            dialogBuilder.dismiss()
        }
    }

    private fun getShoppingList(): List<ShoppingList> {
        return listOf(
            ShoppingList(
                1,
                "First",
                1,
                "SC001",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                true
            ),
            ShoppingList(
                2,
                "Second",
                2,
                "SC002",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                true
            ),
            ShoppingList(
                3,
                "Third",
                1,
                "SC003",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                false
            ),
            ShoppingList(
                4,
                "Fourth",
                1,
                "SC004",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                false
            ),
        )
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
                (dialogBuilder as? AlertDialog)?.findViewById<TextInputLayout>(R.id.etShoppingListRename)
            textInputLayout?.editText?.doAfterTextChanged {
                textInputLayout.clearErrorIfRequiredValid(requireActivity())
            }
            if (!textInputLayout!!.checkAndShowIfRequiredFilled(requireActivity())) {
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
