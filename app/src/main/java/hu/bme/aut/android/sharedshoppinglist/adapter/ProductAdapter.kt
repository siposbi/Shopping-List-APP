package hu.bme.aut.android.sharedshoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.databinding.ItemProductBinding
import hu.bme.aut.android.sharedshoppinglist.model.Product
import hu.bme.aut.android.sharedshoppinglist.util.submitRemoveAt
import hu.bme.aut.android.sharedshoppinglist.util.submitUpdateAt

class ProductAdapter(private val context: Context) :
    ListAdapter<Product, ProductAdapter.ViewHolder>(itemCallback) {

    private var products = emptyList<Product>()

    lateinit var onInsertListener: OnInsertListener

    var productListener: ProductListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.product = product

        if (product.isShared) {
            holder.binding.ivIsSharedImage.setImageResource(R.drawable.ic_baseline_group_48)
        }
        holder.binding.tvName.text = product.Name
        holder.binding.tvPrice.text = product.ActualPrice?.toString() ?: product.PlannedPrice.toString()
        if (product.BoughtByID != null) {
            holder.binding.ivIsBought.setImageResource(R.drawable.ic_baseline_check_24)
        } else {
            holder.binding.ivIsBought.setImageResource(0)
        }
        holder.binding.ivIsSharedImage.tooltipText = if (product.isShared) {
            context.getString(R.string.for_everyone)
        } else {
            //TODO USE ACTUAL NAME
            context.getString(R.string.for_selected_member, product.AddedByID.toString())
        }
    }

    fun clear() {
        products = emptyList()
        submitList(products)
    }

    fun setProducts(productsIn: List<Product>) {
        products += productsIn
        submitList(products)
    }

    fun deleteProduct(product: Product) {
        products -= product
        submitList(products)
    }

    fun addProduct(product: Product, position: Int = 0) {
        val tmpList = products.toMutableList()
        tmpList.add(position, product)
        products = tmpList
        submitList(products)
        if (position == 0) {
            onInsertListener.scrollToTop()
        }
    }

    fun deleteProductByPosition(adapterPosition: Int, user: Any) {
        val product = products[adapterPosition]
        if (product.AddedByID != user) {
            productListener?.showSnackBarFromAdapter(R.string.you_cant_delete)
            notifyItemChanged(adapterPosition)
        } else if (productListener!!.onItemDelete(product, adapterPosition)) {
            products = submitRemoveAt(products, adapterPosition)
        }
    }

    // TODO Show modal to ask for price
    fun purchaseProductByPosition(adapterPosition: Int, user: Any) {
        val product = products[adapterPosition].copy()
        if (product.BoughtByID != null) {
            if (product.BoughtByID != user) {
                productListener?.showSnackBarFromAdapter(R.string.you_cant_purchase)
                notifyItemChanged(adapterPosition)
            } else if (productListener!!.onItemPurchasedUndo(product)) {
                product.BoughtByID = null
                products = submitUpdateAt(products, product, adapterPosition)
            }
        } else if (productListener!!.onItemPurchased(product)) {
            // TODO ACTUALLY SET ID
            product.BoughtByID = 2L
            products = submitUpdateAt(products, product, adapterPosition)
        }
    }

    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var product: Product? = null

        init {
            itemView.setOnLongClickListener {
                product?.let { productListener?.onItemLongClick(it) }
                true
            }
        }
    }

    interface ProductListener {
        fun onItemLongClick(product: Product)
        fun onItemDelete(product: Product, position: Int): Boolean
        fun onItemPurchased(product: Product): Boolean
        fun onItemPurchasedUndo(product: Product): Boolean
        fun showSnackBarFromAdapter(resourceString: Int)
    }

    interface OnInsertListener {
        fun scrollToTop()
    }

    companion object {
        object itemCallback : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.ID == newItem.ID
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.BoughtByID == newItem.BoughtByID
            }
        }
    }
}