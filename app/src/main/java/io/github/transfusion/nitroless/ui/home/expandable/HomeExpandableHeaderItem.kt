package io.github.transfusion.nitroless.ui.home.expandable

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IClickable
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.ui.home.ITEM_VIEW_TYPE_HEADER

// https://github.com/mikepenz/FastAdapter/issues/752
// issue with data binding
open class HomeExpandableHeaderItem(public val name: String) :
    AbstractExpandableItem<HomeExpandableHeaderItem.ViewHolder>(),
    IClickable<HomeExpandableHeaderItem>, ISubItem<HomeExpandableHeaderItem.ViewHolder> {

    private var mOnClickListener: ClickListener<HomeExpandableHeaderItem>? = null

    override val type: Int
        get() = ITEM_VIEW_TYPE_HEADER

    override val layoutRes: Int
        get() = R.layout.home_recyclerview_source_header


    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        val ctx = holder.itemView.context
        holder.sourceNameTextView.text = name
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.sourceNameTextView.text = null
    }


    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override var onItemClickListener: ClickListener<HomeExpandableHeaderItem>? =
        { view: View?, iAdapter: IAdapter<HomeExpandableHeaderItem>, homeExpandableHeaderItem: HomeExpandableHeaderItem, position: Int ->
            Log.d(javaClass.name, "clicked home header position $position")


            mOnClickListener?.invoke(view, iAdapter, homeExpandableHeaderItem, position) ?: true
        }
        set(onClickListener) {
            this.mOnClickListener = onClickListener // on purpose
        }

    override var onPreItemClickListener: ClickListener<HomeExpandableHeaderItem>?
        get() = null
        set(_) {}

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var sourceNameTextView: TextView = view.findViewById(R.id.home_source_name)
//        var icon: ImageView = view.findViewById(R.id.material_drawer_icon)
    }


}