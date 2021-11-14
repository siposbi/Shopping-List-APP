package hu.bme.aut.android.sharedshoppinglist.fragment

import android.os.Bundle
import android.view.*
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.adapter.ExportAdapter
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentExportListBinding
import hu.bme.aut.android.sharedshoppinglist.model.Export
import hu.bme.aut.android.sharedshoppinglist.network.ShoppingListClient
import hu.bme.aut.android.sharedshoppinglist.util.showSnackBar
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class ExportFragment : Fragment(), ExportAdapter.ExportAdapterListener {
    private var _binding: FragmentExportListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ExportAdapter
    private val args: ExportFragmentArgs by navArgs()
    private lateinit var apiClient: ShoppingListClient
    private var lastSelected: Pair<Long, Long>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportListBinding.inflate(inflater, container, false)
        apiClient = ShoppingListClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        setHasOptionsMenu(true)

        adapter = ExportAdapter(this, requireContext())
        binding.recyclerView.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.recyclerView.adapter = adapter

        binding.banner.setRightButtonAction { showDatePicker() }
        binding.banner.show()
        binding.recyclerView.setOnRetryClickListener {
            lastSelected?.let { getExport(it.first, it.second) }
        }
        binding.recyclerView.showEmptyView(getString(R.string.no_date_selected))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_export, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_select_date_range -> {
            showDatePicker()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onExportsLoaded(exports: List<Export>) {
        adapter.setExports(exports)
    }

    private fun onExportsLoadFailed(error: String) {
        binding.recyclerView.showErrorView()
        showSnackBar(error, length = Snackbar.LENGTH_LONG)
    }

    override fun itemCountCallback(count: Int) {
        when (count) {
            0 -> binding.recyclerView.showEmptyView()
            else -> binding.recyclerView.hideAllViews()
        }
    }

    private fun showDatePicker() {
        val constraintsBuilder =
            CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.date_selector_title)
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        dateRangePicker.show(parentFragmentManager, "DATE_RANGE_PICKER_DIALOG")

        dateRangePicker.addOnPositiveButtonClickListener {
            binding.recyclerView.showLoadingView()
            binding.tvSelectedDate.visibility = View.VISIBLE
            lastSelected = it
            getExport(it.first, it.second)
            val date1 = localDateTimeFromLong(it.first)
            val date2 = localDateTimeFromLong(it.second)
            val date1String =
                getString(R.string.date_format, date1.year, date1.monthValue, date1.dayOfMonth)
            val date2String =
                getString(R.string.date_format, date2.year, date2.monthValue, date2.dayOfMonth)
            binding.tvSelectedDate.text = "$date1String - $date2String"
            binding.banner.dismiss()
            adapter.clear()
        }
    }

    private fun localDateTimeFromLong(epochMilli: Long): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(epochMilli),
            TimeZone.getDefault().toZoneId()
        )
    }

    private fun getExport(startDate: Long, endDate: Long) {
        apiClient.getExport(
            listId = args.shoppingListId,
            startDate = localDateTimeFromLong(startDate),
            endDate = localDateTimeFromLong(endDate),
            onSuccess = ::onExportsLoaded,
            onError = ::onExportsLoadFailed
        )
    }
}