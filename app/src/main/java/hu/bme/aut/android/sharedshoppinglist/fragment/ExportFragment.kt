package hu.bme.aut.android.sharedshoppinglist.fragment

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.adapter.ExportAdapter
import hu.bme.aut.android.sharedshoppinglist.databinding.FragmentExportBinding
import hu.bme.aut.android.sharedshoppinglist.model.Export
import java.util.*
import kotlin.random.Random

// TODO Amíg nincs váalsztva semmi, mutassa, hogy válasszunk üzenetet
// TODO Loading charging icon, de ezt mondjuk mindenhová kéne
class ExportFragment : Fragment() {
    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ExportAdapter
    private val args: ExportFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        setHasOptionsMenu(true)

        adapter = ExportAdapter(requireContext())
        binding.rvExportsList.layoutManager = LinearLayoutManager(activity)
        binding.rvExportsList.adapter = adapter

        binding.banner.setLeftButtonAction { binding.banner.dismiss() }
        binding.banner.setRightButtonAction  {
            showDatePicker()
            // TODO CHECK IS SELECTED
            binding.banner.dismiss()
        }

        binding.banner.show()
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
        R.id.action_save_export -> {
            // TODO SAVE FILE (persze csak ha van mint menteni amúgy)
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

    private fun getExports(): List<Export> {
        return listOf(
            Export(
                Random.nextLong(),
                "FN${Random.nextInt(10)}",
                "LN${Random.nextInt(10)}",
                Random.nextLong(-2000, 2000),
            ),
            Export(
                Random.nextLong(),
                "FN${Random.nextInt(10)}",
                "LN${Random.nextInt(10)}",
                Random.nextLong(-2000, 2000),
            ),
            Export(
                Random.nextLong(),
                "FN${Random.nextInt(10)}",
                "LN${Random.nextInt(10)}",
                Random.nextLong(-2000, 2000),
            ),
            Export(
                Random.nextLong(),
                "FN${Random.nextInt(10)}",
                "LN${Random.nextInt(10)}",
                Random.nextLong(-2000, 2000),
            ),
            Export(
                Random.nextLong(),
                "FN${Random.nextInt(10)}",
                "LN${Random.nextInt(10)}",
                Random.nextLong(-2000, 2000),
            ),
            Export(
                Random.nextLong(),
                "FN${Random.nextInt(10)}",
                "LN${Random.nextInt(10)}",
                Random.nextLong(-2000, 2000),
            ),
        )
    }

    private fun showDatePicker(){
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())

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
            val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
            val date1 = Date(it.first)
            Log.i("EA_SELECTED", format.format(date1))
            val date2 = Date(it.second)
            Log.i("EA_SELECTED", format.format(date2))
            adapter.clear()
            adapter.setExports(getExports())
        }
    }
}