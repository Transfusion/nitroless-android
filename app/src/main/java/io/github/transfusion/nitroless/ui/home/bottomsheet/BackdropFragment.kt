package io.github.transfusion.nitroless.ui.home.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.transfusion.nitroless.adapters.CreditsAdapter
import io.github.transfusion.nitroless.databinding.BackdropFragmentBinding


class BackdropFragment : Fragment() {
    private var _binding: BackdropFragmentBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BackdropFragmentBinding.inflate(inflater, container, false)
        val root = binding.root
        val creditsAdapter = CreditsAdapter()
        subscribeCreditsAdapter(creditsAdapter)

        return root
    }

    private fun subscribeCreditsAdapter(creditsAdapter: CreditsAdapter) {
        var i = 0
        val l = arrayListOf<CreditsDataItem>()
        l.add(
            CreditsDataItem(
                id = i++,
                name = "Alpha_Stream",
                roles = arrayListOf("Idea", "Assets", "Website"),
                github_username = "TheAlphaStream",
                twitter_username = "Kutarin_"
            )
        )
        l.add(
            CreditsDataItem(
                id = i++,
                name = "Paras KCD",
                roles = arrayListOf("Website", "Windows App"),
                github_username = "paraskcd1315",
                twitter_username = "paraskcd"
            )
        )
        l.add(
            CreditsDataItem(
                id = i++,
                name = "Amy",
                roles = arrayListOf("iOS App", "iOS Keyboard", "macOS App"),
                github_username = "elihwyma",
                twitter_username = "elihwyma"
            )
        )
        l.add(
            CreditsDataItem(
                id = i++,
                name = "Althio",
                roles = arrayListOf("macOS App"),
                github_username = "althiometer",
                twitter_username = "a1thio"
            )
        )

        creditsAdapter.submitList(l)
        binding.creditsRecyclerView.adapter = creditsAdapter
    }
}