package io.github.transfusion.nitroless.ui.home

//import io.github.transfusion.nitroless.adapters.HomeSectionedAdapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.github.transfusion.nitroless.NitrolessApplication
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.adapters.HomeFragmentAdapter
import io.github.transfusion.nitroless.adapters.RecentlyUsedEmotesAdapter
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.databinding.FragmentHomeBinding
import io.github.transfusion.nitroless.enums.LOADINGSTATUS
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.storage.RecentlyUsedEmote
import io.github.transfusion.nitroless.ui.home.bottomsheet.BackdropFragment
import io.github.transfusion.nitroless.ui.interfaces.EmoteClickedInterface
import java.util.*


class HomeFragment : Fragment(), EmoteClickedInterface, SearchView.OnQueryTextListener {

    private var mBottomSheetBehavior: BottomSheetBehavior<View>? = null

    private lateinit var homeFragmentAdapter: HomeFragmentAdapter

    private lateinit var recentlyUsedEmotesAdapter: RecentlyUsedEmotesAdapter

    private val homeViewModel: HomeViewModel by viewModels {
        val app = (activity?.application as NitrolessApplication)
        HomeViewModelFactory(app.repository, app.recentlyUsedEmoteRepository)
    }

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString("searchQuery");
        }
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(javaClass.name, "onCreateView! ${resources.configuration.orientation}")
        /*homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)*/

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        /** start toolbar init **/
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_sources
            )
        )

        val toolbar = binding.toolbar
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.inflateMenu(R.menu.home_toolbar_menu)
        /** end toolbar init **/

        /** start init of the draggable bottom sheet **/
        val infoBottomSheet =
            childFragmentManager.findFragmentById(R.id.info_bottom_sheet) as BackdropFragment
        infoBottomSheet.let {
            val bsb = BottomSheetBehavior.from(it.view as View)
            bsb.state = BottomSheetBehavior.STATE_HIDDEN
            mBottomSheetBehavior = bsb
        }

        infoBottomSheet.setOnCloseListener {
            mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_show_info -> {
                    mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
            false
        }
        /** end bottom sheet init **/


        val root: View = binding.root
//        val textView: TextView = binding.textHome
        /*homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        homeViewModel.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                LOADINGSTATUS.LOADING -> {
                    binding.progressBarVisible = true
                }
                else -> {
                    binding.progressBarVisible = false
                }
            }
        }


        val screenWidth = requireContext().resources.displayMetrics.widthPixels

        val emoteCellSide = resources.getDimension(R.dimen.emote_cell_side)
        val emoteCellPadding = resources.getDimension(R.dimen.emote_cell_padding)
        val totalEmoteCellWidth = emoteCellSide + emoteCellPadding * 2
        Log.d(javaClass.name, "width of screen $screenWidth")
        Log.d(javaClass.name, "side of emote cell $emoteCellSide")
        val noOfSpans = (screenWidth / totalEmoteCellWidth).toInt()
        Log.d(javaClass.name, "calculated spans $noOfSpans")


        homeFragmentAdapter =
            HomeFragmentAdapter { nitrolessRepo: NitrolessRepo, path: String, emote: NitrolessRepoEmoteModel ->
                onEmoteClicked(
                    nitrolessRepo,
                    path,
                    emote,

                    binding.homeCoordinatorLayout,
                    requireContext()
                )
                // insert into recently used
                val recentlyUsedEmote = RecentlyUsedEmote(
                    repoId = nitrolessRepo.id,
                    emote_path = path,
                    emote_name = emote.name,
                    emote_type = emote.type,
                    emote_used = Date()
                )
                homeViewModel.insertRecentlyUsed(recentlyUsedEmote)
            }
        recentlyUsedEmotesAdapter =
            RecentlyUsedEmotesAdapter { nitrolessRepo: NitrolessRepo, path: String, emote: NitrolessRepoEmoteModel ->
                onEmoteClicked(
                    nitrolessRepo,
                    path,
                    emote,

                    binding.homeCoordinatorLayout,
                    requireContext()
                )
                // insert into recently used
                val recentlyUsedEmote = RecentlyUsedEmote(
                    repoId = nitrolessRepo.id,
                    emote_path = path,
                    emote_name = emote.name,
                    emote_type = emote.type,
                    emote_used = Date()
                )
                homeViewModel.insertRecentlyUsed(recentlyUsedEmote)
            }

        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(false).setStableIdMode(ConcatAdapter.Config.StableIdMode.NO_STABLE_IDS)
        }.build()

        val concatAdapter = ConcatAdapter(config, recentlyUsedEmotesAdapter, homeFragmentAdapter)

        val gridLayoutManager = GridLayoutManager(requireContext(), noOfSpans)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (concatAdapter.getItemViewType(position)) {
                    ITEM_VIEW_TYPE_EMOTE_ITEM -> 1
                    ITEM_VIEW_TYPE_RECENT_EMOTE_ITEM -> 1
                    ITEM_VIEW_TYPE_MESSAGE_ITEM -> noOfSpans
                    ITEM_VIEW_TYPE_HEADER -> noOfSpans
                    ITEM_VIEW_TYPE_RECENT_HEADER -> noOfSpans
                    else -> -1
                }
            }
        }

        binding.homeRecyclerView.layoutManager = gridLayoutManager
        subscribeHomeFragmentAdapter(homeFragmentAdapter)
        subscribeRecentlyUsedEmotesAdapter(recentlyUsedEmotesAdapter)

        // bind SearchView
        binding.emoteSearch.setOnQueryTextListener(this)

        binding.homeRecyclerView.adapter = concatAdapter
        return root
    }

    private fun subscribeHomeFragmentAdapter(adapter: HomeFragmentAdapter) {
        homeViewModel.nitrolessRepoAndModels.observe(viewLifecycleOwner) {
            adapter.massageDataAndSubmitList(it)
        }
    }

    private fun subscribeRecentlyUsedEmotesAdapter(adapter: RecentlyUsedEmotesAdapter) {
        homeViewModel.recentlyUsedEmoteAndRepos.observe(viewLifecycleOwner) {
            adapter.massageDataAndSubmitList(it)
        }
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

        /*homeViewModel.nitrolessRepoAndModels.observe(viewLifecycleOwner) {
            if (BuildConfig.DEBUG)
                Log.d(javaClass.name, it.toString())
        }*/
    }

    private var mSearchQuery: String? = null
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("searchQuery", mSearchQuery);
        super.onSaveInstanceState(outState)
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        this.onQueryTextChange(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        mSearchQuery = newText
        homeFragmentAdapter.filter.filter(newText);
        return false
    }

}