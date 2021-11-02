package hu.bme.aut.android.sharedshoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.databinding.ItemExportBinding
import hu.bme.aut.android.sharedshoppinglist.model.Export
import kotlin.math.absoluteValue

class ExportAdapter(private val context: Context) :
    ListAdapter<Export, ExportAdapter.ViewHolder>(itemCallback) {

    private var exports = emptyList<Export>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemExportBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val export = exports[position]
        holder.export = export

        holder.binding.tvUserName.text =
            context.getString(R.string.item_user_first_last_name, export.firstName, export.lastName)
        holder.binding.tvExplanation.text =
            context.getString(if (export.money >= 0) R.string.export_explanation_positive_balance else R.string.export_explanation_negative_balance)
        val priceAsString = export.money.absoluteValue.toString()
        val dollars = priceAsString.substring(0, priceAsString.length - 2)
        val cents = priceAsString.substring(priceAsString.length - 2)
        holder.binding.tvBalance.text =
            context.getString(R.string.item_member_export_price, dollars, cents)
        holder.binding.tvBalance.setTextColor(context.getColor(if (export.money >= 0) R.color.positive_balance else R.color.negative_balance))
    }

    fun setExports(exportIn: List<Export>) {
        exports += exportIn
        submitList(exports)
    }

    fun clear() {
        exports = emptyList()
        submitList(exports)
    }

    inner class ViewHolder(val binding: ItemExportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var export: Export? = null
    }

    companion object {
        object itemCallback : DiffUtil.ItemCallback<Export>() {
            override fun areItemsTheSame(oldItem: Export, newItem: Export): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Export, newItem: Export): Boolean {
                return oldItem == newItem
            }
        }
    }
}