package io.github.transfusion.nitroless.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.expandable.ExpandableExtension
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import io.github.transfusion.nitroless.NitrolessApplication
import io.github.transfusion.nitroless.R
//import io.github.transfusion.nitroless.adapters.HomeSectionedAdapter
import io.github.transfusion.nitroless.databinding.FragmentHomeBinding
import io.github.transfusion.nitroless.ui.home.expandable.HomeExpandableHeaderItem


const val ITEM_VIEW_TYPE_HEADER = 10010
const val ITEM_VIEW_TYPE_EMOTE_ITEM = 10011
const val ITEM_VIEW_TYPE_MESSAGE_ITEM = 10012 // e.g. "repo failed to load"

class HomeFragment : Fragment() {

    private lateinit var fastItemAdapter: GenericFastItemAdapter

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((activity?.application as NitrolessApplication).repository)
    }

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(javaClass.name, "onCreateView! ${resources.configuration.orientation}")
        /*homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)*/

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
        /*homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        val screenWidth = requireContext().resources.displayMetrics.widthPixels

        val emoteCellSide = resources.getDimension(R.dimen.emote_cell_side)
        val emoteCellPadding = resources.getDimension(R.dimen.emote_cell_padding)
        val totalEmoteCellWidth = emoteCellSide + emoteCellPadding * 2
        Log.d(javaClass.name, "width of screen $screenWidth")
        Log.d(javaClass.name, "side of emote cell $emoteCellSide")
        val noOfSpans = (screenWidth / totalEmoteCellWidth).toInt()
        Log.d(javaClass.name, "calculated spans $noOfSpans")


        fastItemAdapter = FastItemAdapter()

        // enable expandables
        val expandableExtension = fastItemAdapter.getExpandableExtension()
        val gridLayoutManager = GridLayoutManager(requireContext(), noOfSpans)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (fastItemAdapter.getItemViewType(position)) {
                    ITEM_VIEW_TYPE_EMOTE_ITEM -> 1
                    ITEM_VIEW_TYPE_MESSAGE_ITEM -> noOfSpans
                    ITEM_VIEW_TYPE_HEADER -> noOfSpans
                    else -> -1
                }
            }
        }

        binding.homeRecyclerView.layoutManager = gridLayoutManager
//        binding.homeRecyclerView.itemAnimator = SlideDownAlphaAnimator()
//        binding.homeRecyclerView.adapter = fastItemAdapter

        subscribeHomeSectionedAdapter(expandableExtension, fastItemAdapter)


        return root
    }

    private fun subscribeHomeSectionedAdapter(
        expandableExtension: ExpandableExtension<GenericItem>,
        adapter: GenericFastItemAdapter
    ) {
        homeViewModel.nitrolessRepoAndModels.observe(viewLifecycleOwner) {
            val items = ArrayList<HomeExpandableHeaderItem>(it.size)
            for (nitrolessRepoAndModel in it) {
//                if (nitrolessRepoAndModel.nitrolessRepoModel == null) continue
                val homeExpandableHeaderItem =
                    HomeExpandableHeaderItem(nitrolessRepoAndModel.nitrolessRepo)

                if (nitrolessRepoAndModel.nitrolessRepoModel != null) {
                    val emotes = ArrayList<BindingEmoteCellItem>()
                    for (emote in nitrolessRepoAndModel.nitrolessRepoModel.emotes) {
                        val bindingEmoteCellItem =
                            BindingEmoteCellItem(
                                nitrolessRepoAndModel.nitrolessRepo.url,
                                nitrolessRepoAndModel.nitrolessRepoModel.path,
                                emote
                            )
                        emotes.add(bindingEmoteCellItem)
                    }
                    homeExpandableHeaderItem.subItems.addAll(emotes)
                } else {
                    val msg = BindingMessageItem(R.string.repo_fetch_error)
                    homeExpandableHeaderItem.subItems.add(msg)
                }


                items.add(homeExpandableHeaderItem)
            }
            adapter.setNewList(items)
            expandableExtension.withSavedInstanceState(getAllExpandableIdsBundle(items, ""), "")
        }
        binding.homeRecyclerView.adapter = adapter
    }

    // https://github.com/mikepenz/FastAdapter/issues/502
    private fun getAllExpandableIdsBundle(
        items: List<IItem<*>>,
        fastAdapterBundlePrefix: String
    ): Bundle {
        val ids = getAllExpandableIds(items)
        return Bundle().apply {
            putLongArray("bundle_expanded$fastAdapterBundlePrefix", ids.toLongArray())
        }
    }

    private fun getAllExpandableIds(items: List<IItem<*>>?): ArrayList<Long> {
        val ids = ArrayList<Long>()
        items?.forEach {
            if (it is IExpandable<*>) {
                ids.add(it.identifier)
                ids.addAll(getAllExpandableIds(it.subItems).toList())
            }
        }
        return ids
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_sources
//                , R.id.navigation_notifications
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        homeViewModel.nitrolessRepoAndModels.observe(viewLifecycleOwner) {
            Log.d(javaClass.name, "nasty loaded!!")
            Log.d(javaClass.name, it.toString())
        }
    }

}