package hu.bme.aut.android.sharedshoppinglist.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import hu.bme.aut.android.sharedshoppinglist.R
import hu.bme.aut.android.sharedshoppinglist.databinding.LceeRecyclerLayoutBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.RecyclerEmptyLayoutBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.RecyclerErrorLayoutBinding
import hu.bme.aut.android.sharedshoppinglist.databinding.RecyclerLoadingLayoutBinding

/**
 * Created by suson on 6/27/20
 * Custom recycler view with integrated error, empty and loading view
 * https://susuthapa19961227.medium.com/recycler-view-with-empty-view-loading-view-and-error-view-1266c34c1504
 * Modified by Balázs Sipos: Added SwipeRefreshLayout to RecyclerView
 */
class LCEERecyclerView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)


    private val binding: LceeRecyclerLayoutBinding =
        LceeRecyclerLayoutBinding.inflate(LayoutInflater.from(context), this)

    private val errorBinding: RecyclerErrorLayoutBinding = binding.customErrorView
    private val emptyBinding: RecyclerEmptyLayoutBinding = binding.customEmptyView
    private val loadingBinding: RecyclerLoadingLayoutBinding = binding.customOverlayView

    private var refreshLayoutPreference: Boolean

    // expose the recycler view
    val recyclerView: RecyclerView
        get() = binding.customRecyclerView

    val refreshLayout: SwipeRefreshLayout
        get() = binding.refreshLayout

    var errorText: String = ""
        set(value) {
            field = value
            errorBinding.errorMsgText.text = value
        }

    var emptyText: String = ""
        set(value) {
            field = value
            emptyBinding.emptyMessage.text = value
        }

    @DrawableRes
    var errorIcon = 0
        set(value) {
            field = value
            errorBinding.errorImage.setImageResource(value)
        }

    @DrawableRes
    var emptyIcon = 0
        set(value) {
            field = value
            emptyBinding.emptyImage.setImageResource(value)
        }

    init {

        // inflate the layout

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LCEERecyclerView,
            0,
            0
        ).apply {
            try {
                errorText =
                    getString(R.styleable.LCEERecyclerView_errorText) ?: "Something went wrong"
                emptyText =
                    getString(R.styleable.LCEERecyclerView_emptyText) ?: "Nothing to show"
                errorIcon = getResourceId(
                    R.styleable.LCEERecyclerView_errorIcon,
                    R.drawable.ic_error_loading
                )
                emptyIcon =
                    getResourceId(R.styleable.LCEERecyclerView_emptyIcon, R.drawable.ic_empty_image)
                val refreshLayoutSetting =
                    getBoolean(R.styleable.LCEERecyclerView_refreshable, true)
                refreshLayout.isEnabled = refreshLayoutSetting
                refreshLayoutPreference = refreshLayoutSetting
            } finally {
                recycle()
            }
        }
    }

    fun showEmptyView(msg: String? = null) {
        emptyText = msg ?: emptyText
        disableRefreshLayout()
        loadingBinding.root.visibility = View.GONE
        errorBinding.root.visibility = View.GONE

        emptyBinding.root.visibility = View.VISIBLE
    }

    fun showErrorView(msg: String? = null) {
        errorText = msg ?: errorText
        disableRefreshLayout()
        loadingBinding.root.visibility = View.GONE
        emptyBinding.root.visibility = View.GONE

        errorBinding.root.visibility = View.VISIBLE
    }

    fun showLoadingView() {
        disableRefreshLayout()
        emptyBinding.root.visibility = View.GONE
        errorBinding.root.visibility = View.GONE

        loadingBinding.root.visibility = View.VISIBLE
    }

    fun hideAllViews() {
        loadingBinding.root.visibility = View.GONE
        errorBinding.root.visibility = View.GONE
        emptyBinding.root.visibility = View.GONE
        refreshLayout.isEnabled = refreshLayoutPreference
        refreshLayout.isRefreshing = false
    }

    fun setOnRetryClickListener(callback: () -> Unit) {
        errorBinding.retryButton.setOnClickListener {
            callback()
        }
    }

    private fun disableRefreshLayout() {
        refreshLayout.isEnabled = false
        refreshLayout.isRefreshing = false
    }
}