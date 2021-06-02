package io.github.transfusion.nitroless.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import io.github.transfusion.nitroless.NitrolessApplication
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.adapters.HomeSectionedAdapter
import io.github.transfusion.nitroless.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

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
    ): View? {
        /*homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)*/

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
        /*homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        val homeSectionedAdapter = HomeSectionedAdapter()
        subscribeHomeSectionedAdapter(homeSectionedAdapter)
        return root
    }

    private fun subscribeHomeSectionedAdapter(adapter: HomeSectionedAdapter) {
        homeViewModel.nitrolessRepoAndModels.observe(viewLifecycleOwner) {
            adapter.addHeadersAndSubmitList(it)
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

}