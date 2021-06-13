package io.github.transfusion.nitroless.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.transfusion.nitroless.NitrolessApplication
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.data.NitrolessRepoModel
import io.github.transfusion.nitroless.databinding.EmoteCellBinding
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewSourceHeaderBinding
import io.github.transfusion.nitroless.databinding.HomeRecyclerviewSourceMessageBinding
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.ui.home.*
import io.github.transfusion.nitroless.viewholders.EmoteCellViewHolder
import io.github.transfusion.nitroless.viewholders.HomeHeaderViewHolder
import io.github.transfusion.nitroless.viewholders.HomeMessageItemViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragmentAdapter(val onEmoteClicked: (NitrolessRepo, String, NitrolessRepoEmoteModel) -> Unit) :
    Filterable,
    ListAdapter<DataItem, RecyclerView.ViewHolder>(
        HomeSectionedDiffCallback()
    ) {
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    currentFilteredBackingDataItems = currentBackingDataItems
                } else {
                    currentBackingDataItems?.let {
                        val filteredList = arrayListOf<DataItem>()
                        for (dataItem in currentBackingDataItems!!) {
                            if (dataItem.type == ITEM_VIEW_TYPE_EMOTE_ITEM) {
                                val casted = dataItem as DataItem.EmoteItem
                                if (charString.toLowerCase(Locale.ENGLISH) in casted.emote.name.toLowerCase(
                                        Locale.ENGLISH
                                    )
                                ) filteredList.add(dataItem)
                            } else {
                                filteredList.add(dataItem)
                            }
                        }
                        currentFilteredBackingDataItems = filteredList
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = currentFilteredBackingDataItems
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                if (results.values == null) return
                currentFilteredBackingDataItems = results.values as List<DataItem>
                updateRenderedList()
            }

        }
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private var currentBackingList: List<NitrolessRepoAndModel>? = null
    private var currentBackingDataItems: List<DataItem>? = null
    private var currentFilteredBackingDataItems: List<DataItem>? = null

    // map of GROUP INDEX (should be the same size as List<NitroLessRepoAndModel below)
    // to expanded state (default all)
    val expandedMap: HashMap<UUID, Boolean> = HashMap()
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
                    super.submitList(dataItems)
                    currentBackingDataItems = dataItems
                    currentFilteredBackingDataItems = dataItems
//                    notifyItemRangeChanged(0, list.size)
                    notifyDataSetChanged()
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
        for (dataItem in currentFilteredBackingDataItems!!) {
            if (dataItem.type == ITEM_VIEW_TYPE_HEADER) {
                val casted =
                    (dataItem as DataItem.HeaderItem).copy() // lmao, DiffUtil checks identity.
                casted.expanded = expandedMap[casted.groupIndex] == true
                result.add(casted)
            } else if (dataItem.type == ITEM_VIEW_TYPE_EMOTE_ITEM) {
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
                holder.bind(item.nitrolessRepo, item.expanded) {
                    onHomeHeaderViewClicked(item)
                }
            }

            is HomeMessageItemViewHolder -> {
                val item = getItem(position) as DataItem.MessageItem
                holder.bind(item.message)
            }

            is EmoteCellViewHolder -> {
                val item = getItem(position) as DataItem.EmoteItem
                holder.bind(item.nitrolessRepo.url, item.nitrolessRepoModel.path, item.emote) {
                    onEmoteClicked(item.nitrolessRepo, item.nitrolessRepoModel.path, item.emote)
                }
            }
        }
    }

}