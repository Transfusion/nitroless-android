package io.github.transfusion.nitroless.ui.sources

import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.github.transfusion.nitroless.viewholders.NitrolessRepoViewHolder
import kotlin.math.min

const val ZERO = 0.0.toFloat()

open class ItemTouchHelperCallback : CustomItemTouchHelper.Callback() {

    lateinit var itemTouchHelper: CustomItemTouchHelper

    /**
     * setup drag and swipe directions
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // vertical dragging
        // val dragFlags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val dragFlags = 0
        // swipe to the left to reveal
        val swipeFlags: Int = ItemTouchHelper.START
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    /**
     * called after dragging ends (finger is lifted)
     *
     * @param recyclerView
     * @param viewHolder   item being dragged
     * @param viewHolder1  target item
     * @return
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    /**
     * horizontal swiping
     *
     * @param viewHolder
     * @param swipeDir
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
        val myViewHolder = viewHolder as NitrolessRepoViewHolder
        myViewHolder.swipedOpen = true
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        _dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (dY != 0f && _dX == 0f) {
            // we are only concerned about horizontal swiping; the conditions of which are
            // dX != 0f; default to ItemTouchUIUtilImpl for other cases
            super.onChildDraw(c, recyclerView, viewHolder, _dX, dY, actionState, isCurrentlyActive)
            return
        }

        var dX: Float = _dX
        val myViewHolder = viewHolder as NitrolessRepoViewHolder
        /*Log.d(
            javaClass.name,
            "current child ${myViewHolder.binding.repo?.name} dX value $_dX swipedopen ${myViewHolder.swipedOpen} limit ${myViewHolder.binding.llRemove.width} maxw ${myViewHolder.binding.actualSourceItem.width}"
        )*/

        if (myViewHolder.swipedOpen) {
            dX += myViewHolder.binding.actualSourceItem.width - myViewHolder.binding.llRemove.width
            itemTouchHelper.setDx(dX)
            myViewHolder.swipedOpen = false
        }

        // truncate if exceed the width of the buttons layout to the left
        if (dX < -myViewHolder.binding.llRemove.width) {
            dX = (-myViewHolder.binding.llRemove.width).toFloat()
        }

        val res = min(dX, ZERO)
        myViewHolder.binding.actualSourceItem.translationX = res

    }

    fun getItemFrontView(mPreOpened: RecyclerView.ViewHolder?): View? {
        val myViewHolder = mPreOpened as NitrolessRepoViewHolder
        return myViewHolder.binding.actualSourceItem
    }
}
