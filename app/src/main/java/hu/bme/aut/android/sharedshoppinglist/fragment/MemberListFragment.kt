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

class MemberListFragment : Fragment() {
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

        adapter = MemberAdapter(requireContext())
        binding.rvMembersList.layoutManager = LinearLayoutManager(activity)
        binding.rvMembersList.adapter = adapter
        loadMembers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadMembers() {
        apiClient.getMembers(
            listId = args.shoppingListId,
            onSuccess = ::onMembersLoaded,
            onError = ::onMembersLoadFailed
        )
    }

    private fun onMembersLoaded(members: List<Member>) {
        binding.loadingIndicator.visibility = View.GONE
        adapter.setMembers(members)
    }

    private fun onMembersLoadFailed(error: String) {
        binding.loadingIndicator.visibility = View.GONE
        showSnackBar(error, length = Snackbar.LENGTH_LONG)
    }
}