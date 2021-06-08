package io.github.transfusion.nitroless.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.transfusion.nitroless.NitrolessApplication
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.databinding.EmoteCellBinding
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewSourceHeaderBinding
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewSourceMessageBinding
import io.github.transfusion.nitroless.ui.home.*
import io.github.transfusion.nitroless.viewholders.EmoteCellViewHolder
import io.github.transfusion.nitroless.viewholders.HomeHeaderViewHolder
import io.github.transfusion.nitroless.viewholders.HomeMessageItemViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragmentAdapter : Filterable, ListAdapter<DataItem, RecyclerView.ViewHolder>(
    HomeSectionedDiffCallback()
) {
    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private var currentBackingList: List<NitrolessRepoAndModel>? = null
    private var currentBackingDataItems: List<DataItem>? = null

    // map of GROUP INDEX (should be the same size as List<NitroLessRepoAndModel below)
    // to expanded state (default all)
    val expandedMap: HashMap<Int, Boolean> = HashMap()
    fun massageDataAndSubmitList(list: List<NitrolessRepoAndModel>) {
        currentBackingList = list
        adapterScope.launch {
            expandedMap.clear()
            val dataItems = ArrayList<DataItem>()
            var id = 0
            for (nitrolessRepoAndModel in list) {

                // append the header
                val header =
                    DataItem.HeaderItem(
                        id++,
                        groupIndex = nitrolessRepoAndModel.nitrolessRepo.id,
                        nitrolessRepo = nitrolessRepoAndModel.nitrolessRepo,
                        subItems = if (nitrolessRepoAndModel.nitrolessRepoModel == null) 1 else nitrolessRepoAndModel.nitrolessRepoModel.emotes.size
                    )
                dataItems.add(header)
                expandedMap[nitrolessRepoAndModel.nitrolessRepo.id] =
                    true // all sections expanded by default

                // append an error message if the model is null else add all the emotes
                if (nitrolessRepoAndModel.nitrolessRepoModel == null) {
                    dataItems.add(
                        DataItem.MessageItem(
                            id++,
                            NitrolessApplication.applicationContext().resources.getString(R.string.repo_fetch_error)
                        )
                    )
                } else {
                    // add all the emotes
                    for (emote in nitrolessRepoAndModel.nitrolessRepoModel.emotes) {
                        dataItems.add(
                            DataItem.EmoteItem(
                                id++,
                                nitrolessRepo = nitrolessRepoAndModel.nitrolessRepo,
                                nitrolessRepoModel = nitrolessRepoAndModel.nitrolessRepoModel,
                                emote
                            )
                        )
                    }
                }

                withContext(Dispatchers.Main) {
                    submitList(dataItems)
                    currentBackingDataItems = dataItems
                    notifyItemRangeChanged(0, list.size)
                }
            }
        }
    }

    // testing strategy - mock currentBackingList
    // prereq  - currentBackingList is not null
    private fun updateRenderedList() {
        // to COLLAPSE, notifyItemRangeRemoved (int positionStart, int itemCount)
        // ListAdapter should call it for us!!
        val result: MutableList<DataItem> = ArrayList()
        for (dataItem in currentBackingDataItems!!) {
            if (dataItem.type == ITEM_VIEW_TYPE_EMOTE_ITEM) {
                val casted = dataItem as DataItem.EmoteItem
                if (expandedMap[casted.nitrolessRepo.id] == true) result.add(dataItem)
            } else {
                result.add(dataItem)
            }
        }
        submitList(result)
    }

    private fun onHomeHeaderViewClicked(
        headerItem: DataItem.HeaderItem
    ) {
        if (currentBackingList == null) return
        if (!expandedMap.containsKey(headerItem.groupIndex)) return
        val groupExpanded = expandedMap.getOrElse(headerItem.groupIndex, { true })
        expandedMap[headerItem.groupIndex] = !groupExpanded
        updateRenderedList()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HomeHeaderViewHolder(
                HomeRecyclerviewSourceHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            ITEM_VIEW_TYPE_MESSAGE_ITEM -> HomeMessageItemViewHolder(
                HomeRecyclerviewSourceMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ITEM_VIEW_TYPE_EMOTE_ITEM -> EmoteCellViewHolder(
                EmoteCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return item.type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomeHeaderViewHolder -> {
                val item = getItem(position) as DataItem.HeaderItem
                holder.bind(item.nitrolessRepo) {
                    onHomeHeaderViewClicked(item)
                }
            }

            is HomeMessageItemViewHolder -> {
                val item = getItem(position) as DataItem.MessageItem
                holder.bind(item.message)
            }

            is EmoteCellViewHolder -> {
                val item = getItem(position) as DataItem.EmoteItem
                holder.bind(item.nitrolessRepo.url, item.nitrolessRepoModel.path, item.emote)
            }
        }
    }

}