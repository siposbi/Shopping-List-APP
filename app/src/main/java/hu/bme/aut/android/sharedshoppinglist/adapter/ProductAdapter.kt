package hu.bme.aut.android.sharedshoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.databinding.ItemProductBinding
import hu.bme.aut.android.sharedshoppinglist.model.ProductMinimal
import hu.bme.aut.android.sharedshoppinglist.util.submitRemoveAt
import hu.bme.aut.android.sharedshoppinglist.util.submitUpdateAt

class ProductAdapter(private val context: Context) :
    ListAdapter<ProductMinimal, ProductAdapter.ViewHolder>(itemCallback) {

    private var products = emptyList<ProductMinimal>()

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
        holder.binding.tvName.text = product.name
        holder.binding.tvPrice.text = product.price.toString()
        if (product.isBought) {
            holder.binding.ivIsBought.setImageResource(R.drawable.ic_baseline_check_24)
        } else {
            holder.binding.ivIsBought.setImageResource(0)
        }
        holder.binding.ivIsSharedImage.tooltipText = if (product.isShared) {
            context.getString(R.string.for_everyone)
        } else {
            //TODO USE ACTUAL NAME
            context.getString(R.string.for_selected_member, "${product.addedByUserFirstName}${product.addedByUserLastName}")
        }
    }

    fun clear() {
        products = emptyList()
        submitList(products)
    }

    fun setProducts(productsIn: List<ProductMinimal>) {
        products += productsIn
        submitList(products)
    }

    fun deleteProduct(product: ProductMinimal) {
        products -= product
        submitList(products)
    }

    fun addProduct(product: ProductMinimal, position: Int = 0) {
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
//        if (product.AddedByID != user) {
//            productListener?.showSnackBarFromAdapter(R.string.you_cant_delete)
//            notifyItemChanged(adapterPosition)
//        } else if (productListener!!.onItemDelete(product, adapterPosition)) {
//            products = submitRemoveAt(products, adapterPosition)
//        }
        if (productListener!!.onItemDelete(product, adapterPosition)){
            products = submitRemoveAt(products, adapterPosition)
        }
    }

    // TODO Show modal to ask for price
    fun purchaseProductByPosition(adapterPosition: Int, user: Any) {
        val product = products[adapterPosition].copy()
//        if (product.BoughtByID != null) {
//            if (product.BoughtByID != user) {
//                productListener?.showSnackBarFromAdapter(R.string.you_cant_purchase)
//                notifyItemChanged(adapterPosition)
//            } else if (productListener!!.onItemPurchasedUndo(product)) {
//                products = submitUpdateAt(products, product, adapterPosition)
//            }
//        } else if (productListener!!.onItemPurchased(product)) {
//            // TODO ACTUALLY SET ID
//            products = submitUpdateAt(products, product, adapterPosition)
//        }
        if (productListener!!.onItemPurchased(product)) {
            // TODO ACTUALLY SET ID
            products = submitUpdateAt(products, product, adapterPosition)
        }
    }

    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var product: ProductMinimal? = null

        init {
            itemView.setOnLongClickListener {
                product?.let { productListener?.onItemLongClick(it) }
                true
            }
            itemView.setOnClickListener {
                product?.let { productListener?.onItemClicked(it) }
            }
        }
    }

    interface ProductListener {
        fun onItemLongClick(product: ProductMinimal)
        fun onItemDelete(product: ProductMinimal, position: Int): Boolean
        fun onItemPurchased(product: ProductMinimal): Boolean
        fun onItemPurchasedUndo(product: ProductMinimal): Boolean
        fun showSnackBarFromAdapter(resourceString: Int)
        fun onItemClicked(product: ProductMinimal)
    }

    interface OnInsertListener {
        fun scrollToTop()
    }

    companion object {
        object itemCallback : DiffUtil.ItemCallback<ProductMinimal>() {
            override fun areItemsTheSame(oldItem: ProductMinimal, newItem: ProductMinimal): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ProductMinimal, newItem: ProductMinimal): Boolean {
                return oldItem == newItem
            }
        }
    }
}