package hu.bme.aut.android.sharedshoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.databinding.ItemMemberBinding
import hu.bme.aut.android.sharedshoppinglist.model.Member
import hu.bme.aut.android.sharedshoppinglist.util.asDateString


class MemberAdapter(
    private val memberAdapterListener: MemberAdapterListener,
    private val context: Context
) :
    ListAdapter<Member, MemberAdapter.ViewHolder>(ItemCallback) {

    private var members = emptyList<Member>()

    inner class ViewHolder(val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var member: Member? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.member = member

        holder.binding.tvUserName.text =
            context.getString(R.string.item_user_first_last_name, member.firstName, member.lastName)
        holder.binding.tvIsOwner.text =
            if (member.isOwner) context.getString(R.string.item_user_is_owner) else ""
        val joinDateString = member.joinDateTime.asDateString(context)
        holder.binding.tvJoinDate.text =
            context.getString(R.string.item_user_join_date, joinDateString)
    }

    fun setMembers(membersIn: List<Member>) {
        members = membersIn
        submitList(members)
        memberAdapterListener.itemCountCallback(members.count())
    }

    interface MemberAdapterListener {
        fun itemCountCallback(count: Int)
    }

    companion object {
        object ItemCallback : DiffUtil.ItemCallback<Member>() {
            override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
                return oldItem == newItem
            }
        }
    }
}