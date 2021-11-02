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
import java.time.format.DateTimeFormatter


class MemberAdapter(private val context: Context) :
    ListAdapter<Member, MemberAdapter.ViewHolder>(itemCallback) {

    private var members = emptyList<Member>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.member = member

        holder.binding.tvUserName.text =
            context.getString(R.string.item_user_first_last_name, member.firstName, member.lastName)
        holder.binding.tvIsOwner.text =
            if (member.isOwner) context.getString(R.string.item_user_is_owner) else ""
        val joinDateString = member.joinDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        holder.binding.tvJoinDate.text = context.getString(R.string.item_user_join_date, joinDateString)
    }

    fun setMembers(membersIn: List<Member>) {
        members += membersIn
        submitList(members)
    }

    inner class ViewHolder(val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var member: Member? = null
    }

    companion object {
        object itemCallback : DiffUtil.ItemCallback<Member>() {
            override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
                return oldItem == newItem
            }
        }
    }
}