package hu.bme.aut.android.sharedshoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.databinding.ItemShoppingListBinding
import hu.bme.aut.android.sharedshoppinglist.model.ShoppingList
import hu.bme.aut.android.sharedshoppinglist.util.asDateTimeString
import hu.bme.aut.android.sharedshoppinglist.util.submitAdd
import hu.bme.aut.android.sharedshoppinglist.util.submitRemoveAt
import hu.bme.aut.android.sharedshoppinglist.util.submitUpdateAt
import java.time.LocalDateTime

class ShoppingListAdapter(
    private val shoppingListCardListener: ShoppingListCardListener,
    private val context: Context
) :
    ListAdapter<ShoppingList, ShoppingListAdapter.ViewHolder>(itemCallback) {

    private var shoppingListList = emptyList<ShoppingList>()

    inner class ViewHolder(val binding: ItemShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var shoppingList: ShoppingList? = null

        init {
            itemView.setOnClickListener {
                shoppingList?.let { shoppingListCardListener.onItemClick(it) }
            }
            itemView.setOnLongClickListener {
                shoppingList?.let { shoppingListCardListener.onItemLongClick(it) }
                true
            }
            binding.btnShare.setOnClickListener {
                shoppingList?.let { shoppingListCardListener.onShareClick(it) }
            }
            binding.btnDelete.setOnClickListener {
                shoppingList?.let {
                    shoppingListCardListener.onDeleteClick(it, absoluteAdapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemShoppingListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shoppingList = shoppingListList[position]
        holder.shoppingList = shoppingList

        if (shoppingList.isShared) {
            holder.binding.ivIsSharedImage.setImageResource(R.drawable.ic_baseline_group_48)
        } else {
            holder.binding.ivIsSharedImage.setImageResource(R.drawable.ic_baseline_person_48)
        }
        holder.binding.tvName.text = shoppingList.name
        holder.binding.tvCreatedAt.text = shoppingList.createdDateTime.asDateTimeString
        if (shoppingList.lastProductAddedDateTime == LocalDateTime.of(1, 1, 1, 0, 0, 0)) {
            holder.binding.tvEditedAt.text = context.getString(R.string.no_products_yet)
        } else {
            holder.binding.tvEditedAt.text = shoppingList.lastProductAddedDateTime.asDateTimeString
        }
    }

    fun setShoppingLists(shoppingLists: List<ShoppingList>) {
        shoppingListList = shoppingLists
        submitList(shoppingListList)
        shoppingListCardListener.itemCountCallback(shoppingListList.count())
    }

    fun deleteShoppingList(shoppingListId: Long) {
        val index = shoppingListList.indexOfFirst { sl -> sl.id == shoppingListId }
        shoppingListList = submitRemoveAt(shoppingListList, index)
        shoppingListCardListener.itemCountCallback(shoppingListList.count())
    }

    fun updateShoppingList(shoppingList: ShoppingList) {
        val index = shoppingListList.indexOfFirst { sl -> sl.id == shoppingList.id }
        shoppingListList = submitUpdateAt(shoppingListList, shoppingList, index)
        shoppingListCardListener.itemCountCallback(shoppingListList.count())
    }

    fun addShoppingList(shoppingList: ShoppingList, position: Int = 0) {
        shoppingListList = submitAdd(shoppingListList, shoppingList, position)
        if (position == 0) {
            shoppingListCardListener.scrollToTop()
        }
        shoppingListCardListener.itemCountCallback(shoppingListList.count())
    }

    interface ShoppingListCardListener {
        fun onItemClick(shoppingList: ShoppingList)
        fun onItemLongClick(shoppingList: ShoppingList)
        fun onShareClick(shoppingList: ShoppingList)
        fun onDeleteClick(shoppingList: ShoppingList, position: Int)
        fun itemCountCallback(count: Int)
        fun scrollToTop()
    }

    companion object {
        object itemCallback : DiffUtil.ItemCallback<ShoppingList>() {
            override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}