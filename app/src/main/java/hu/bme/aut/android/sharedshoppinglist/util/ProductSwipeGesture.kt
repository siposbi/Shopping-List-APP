package hu.bme.aut.android.sharedshoppinglist.util

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.sharedshoppinglist.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class ProductSwipeGesture(
    context: Context,
    private val leftSwipe: (Int) -> Unit,
    private val rightSwipe: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteColor = ContextCompat.getColor(context, R.color.delete_color)
    private val purchaseColor = ContextCompat.getColor(context, R.color.purchase_color)
    private val deleteIcon = R.drawable.ic_baseline_delete_24
    private val purchaseIcon = R.drawable.ic_baseline_add_shopping_cart_24

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) : Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when(direction){
            ItemTouchHelper.LEFT -> {
                leftSwipe(viewHolder.absoluteAdapterPosition)
            }
            ItemTouchHelper.RIGHT -> {
                rightSwipe(viewHolder.absoluteAdapterPosition)
            }
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive        )
            .addSwipeLeftBackgroundColor(deleteColor)
            .addSwipeLeftActionIcon(deleteIcon)
            .addSwipeRightBackgroundColor(purchaseColor)
            .addSwipeRightActionIcon(purchaseIcon)
            .create()
            .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}