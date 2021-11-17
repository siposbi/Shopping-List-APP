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
import hu.bme.aut.android.sharedshoppinglist.util.getPriceAsString
import hu.bme.aut.android.sharedshoppinglist.util.submitAdd
import hu.bme.aut.android.sharedshoppinglist.util.submitRemoveAt
import hu.bme.aut.android.sharedshoppinglist.util.submitUpdateAt

class ProductAdapter(private val productListener: ProductListener, private val context: Context) :
    ListAdapter<ProductMinimal, ProductAdapter.ViewHolder>(ItemCallback) {

    private var products = emptyList<ProductMinimal>()

    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var product: ProductMinimal? = null

        init {
            itemView.setOnClickListener {
                product?.let { productListener.onItemClick(it) }
            }
            itemView.setOnLongClickListener {
                product?.let { productListener.onItemLongClick(it) }
                true
            }
        }
    }

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
        } else {
            holder.binding.ivIsSharedImage.setImageResource(R.drawable.ic_baseline_person_48)
        }
        holder.binding.tvName.text = product.name
        holder.binding.tvPrice.text = product.price.getPriceAsString(context)
        if (product.isBought) {
            holder.binding.ivIsBought.setImageResource(R.drawable.ic_baseline_check_24)
        } else {
            holder.binding.ivIsBought.setImageResource(0)
        }
        holder.binding.ivIsSharedImage.tooltipText = if (product.isShared) {
            context.getString(R.string.for_everyone)
        } else {
            context.getString(
                R.string.for_selected_member,
                "${product.addedByUserFirstName} ${product.addedByUserLastName}"
            )
        }
    }

    fun setProducts(productsIn: List<ProductMinimal>) {
        products = productsIn
        submitList(products)
        productListener.itemCountCallback(products.count())
    }

    fun deleteProduct(productId: Long) {
        val index = products.indexOfFirst { p -> p.id == productId }
        products = submitRemoveAt(products, index)
        productListener.itemCountCallback(products.count())
    }

    fun updateProduct(product: ProductMinimal) {
        val index = products.indexOfFirst { p -> p.id == product.id }
        products = submitUpdateAt(products, product, index)
        productListener.itemCountCallback(products.count())
    }


    fun addProduct(product: ProductMinimal, position: Int = 0) {
        products = submitAdd(products, product, position)
        if (position == 0) {
            productListener.scrollToTop()
        }
        productListener.itemCountCallback(products.count())
    }

    fun getProductByAdapterPosition(adapterPosition: Int): ProductMinimal {
        return products[adapterPosition]
    }

    fun resetSwipe(position: Int) {
        notifyItemChanged(position)
    }

    interface ProductListener {
        fun onItemClick(product: ProductMinimal)
        fun onItemLongClick(product: ProductMinimal)
        fun itemCountCallback(count: Int)
        fun scrollToTop()
    }

    companion object {
        object ItemCallback : DiffUtil.ItemCallback<ProductMinimal>() {
            override fun areItemsTheSame(
                oldItem: ProductMinimal,
                newItem: ProductMinimal
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ProductMinimal,
                newItem: ProductMinimal
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}