package hu.bme.aut.android.sharedshoppinglist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.sharedshoppinglist.adapter.MemberAdapter
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentMemberListBinding
import hu.bme.aut.android.sharedshoppinglist.model.Member
import hu.bme.aut.android.sharedshoppinglist.network.ShoppingListClient
import hu.bme.aut.android.sharedshoppinglist.util.showSnackBar

class MemberListFragment : Fragment(), MemberAdapter.MemberListListener {
    private var _binding: FragmentMemberListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MemberAdapter
    private val args: MemberListFragmentArgs by navArgs()
    private lateinit var apiClient: ShoppingListClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemberListBinding.inflate(inflater, container, false)
        apiClient = ShoppingListClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        adapter = MemberAdapter(this, requireContext())
        binding.recyclerView.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.recyclerView.adapter = adapter

        loadMembers()
        binding.recyclerView.setOnRetryClickListener { loadMembers() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadMembers() {
        binding.recyclerView.showLoadingView()
        apiClient.getMembers(
            listId = args.shoppingListId,
            onSuccess = ::onMembersLoaded,
            onError = ::onMembersLoadFailed
        )
    }

    private fun onMembersLoaded(members: List<Member>) {
        adapter.setMembers(members)
    }

    private fun onMembersLoadFailed(error: String) {
        binding.recyclerView.showErrorView()
        showSnackBar(error, length = Snackbar.LENGTH_LONG)
    }

    override fun itemCountCallback(count: Int) {
        when (count) {
            0 -> binding.recyclerView.showEmptyView()
            else -> binding.recyclerView.hideAllViews()
        }
    }
}