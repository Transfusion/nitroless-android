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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.github.transfusion.nitroless.MainActivity
import io.github.transfusion.nitroless.NitrolessApplication
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.adapters.HomeFragmentAdapter
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.databinding.FragmentHomeBinding
import io.github.transfusion.nitroless.enums.LOADINGSTATUS
import android.view.Gravity

import androidx.coordinatorlayout.widget.CoordinatorLayout


class HomeFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var homeFragmentAdapter: HomeFragmentAdapter

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((activity?.application as NitrolessApplication).repository)
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

    private fun onEmoteClicked(emoteModel: NitrolessRepoEmoteModel) {
        Log.d(javaClass.name, "clicked on ${emoteModel.name}")
        val mySnackbar =
            Snackbar.make(
                binding.homeCoordinatorLayout,
                "Copied ${emoteModel.name}",
                Snackbar.LENGTH_SHORT
            ).setAction("OK") {
                // Responds to click on the action
            }
        // https://stackoverflow.com/questions/31746300/how-to-show-snackbar-at-top-of-the-screen/36768267
        // not material-spec compliant but better than obscuring the
        // bottom row
        val view: View = mySnackbar.view
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.CENTER_HORIZONTAL
        params.width = CoordinatorLayout.LayoutParams.WRAP_CONTENT
        view.layoutParams = params

        mySnackbar.show()

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
            HomeFragmentAdapter { emoteModel: NitrolessRepoEmoteModel -> onEmoteClicked(emoteModel) }

        val gridLayoutManager = GridLayoutManager(requireContext(), noOfSpans)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (homeFragmentAdapter.getItemViewType(position)) {
                    ITEM_VIEW_TYPE_EMOTE_ITEM -> 1
                    ITEM_VIEW_TYPE_MESSAGE_ITEM -> noOfSpans
                    ITEM_VIEW_TYPE_HEADER -> noOfSpans
                    else -> -1
                }
            }
        }

        binding.homeRecyclerView.layoutManager = gridLayoutManager

        subscribeHomeFragmentAdapter(homeFragmentAdapter)

        // bind SearchView
        binding.emoteSearch.setOnQueryTextListener(this)

        return root
    }

    private fun subscribeHomeFragmentAdapter(adapter: HomeFragmentAdapter) {
        homeViewModel.nitrolessRepoAndModels.observe(viewLifecycleOwner) {
            adapter.massageDataAndSubmitList(it)
        }
        binding.homeRecyclerView.adapter = adapter
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