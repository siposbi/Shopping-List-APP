package hu.bme.aut.android.sharedshoppinglist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.sharedshoppinglist.adapter.MemberAdapter
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentMemberListBinding
import hu.bme.aut.android.sharedshoppinglist.model.Member
import java.time.LocalDateTime
import kotlin.random.Random

class MemberListFragment : Fragment() {
    private var _binding: FragmentMemberListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MemberAdapter
    private val args: MemberListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemberListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        adapter = MemberAdapter(requireContext())
        binding.rvMembersList.layoutManager = LinearLayoutManager(activity)
        binding.rvMembersList.adapter = adapter
        adapter.setMembers(getMembers())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getMembers(): List<Member> {
        return listOf(
            Member(
                Random.nextLong(1000),
                "FN${Random.nextInt(10)}",
                "LN${Random.nextInt(10)}",
                LocalDateTime.now(),
                true
            ),
            Member(
                Random.nextLong(1000),
                "FN${Random.nextInt(10)}",
                "LN${Random.nextInt(10)}",
                LocalDateTime.now().minusDays(1),
                false
            ),
            Member(
                Random.nextLong(1000),
                "FN${Random.nextInt(10)}",
                "LN${Random.nextInt(10)}",
                LocalDateTime.now().minusDays(2),
                false
            ),
            Member(
                Random.nextLong(1000),
                "FN${Random.nextInt(10)}",
                "LN${Random.nextInt(10)}",
                LocalDateTime.now().minusDays(3),
                false
            ),
        )
    }
}