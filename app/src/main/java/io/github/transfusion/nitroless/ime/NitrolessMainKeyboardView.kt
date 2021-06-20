package io.github.transfusion.nitroless.ime

import android.inputmethodservice.Keyboard
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.databinding.KeyboardMainBinding
import io.github.transfusion.nitroless.enums.LOADINGSTATUS


class NitrolessMainKeyboardView @JvmOverloads
constructor(
    private val nitrolessInputMethodService: NitrolessInputMethodService,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(nitrolessInputMethodService, attributeSet, defStyleAttr),
    SearchView.OnQueryTextListener {

    private var _binding: KeyboardMainBinding? = null
    private val binding get() = _binding!!

    val emoteSearch get() = binding.emoteSearch
    val inputView get() = binding.keyboardView
    val emotesView get() = binding.emotesView


    init {
        val _ctx = ContextThemeWrapper(nitrolessInputMethodService, R.style.Theme_Nitroless)
        _binding =
            DataBindingUtil.inflate(LayoutInflater.from(_ctx), R.layout.keyboard_main, this, true)

        val kb = Keyboard(nitrolessInputMethodService, R.xml.qwerty)
        binding.keyboardView.keyboard = kb
//        binding.keyboardView.setOnKeyboardActionListener(nitrolessInputMethodService)
        binding.emotesView.initialize(nitrolessInputMethodService)

        binding.backBtn.setOnClickListener {
            handleBackBtn(it)
        }

        binding.emoteSearch.getSearchAutoComplete().setOnFocusChangeListener { v, hasFocus ->
            emoteSearchFocusChangeListener(v, hasFocus)
        }

        binding.emoteSearch.setOnQueryTextListener(this)
    }

    private var _emoteSearchFocused = false
    val emoteSearchFocused get() = _emoteSearchFocused

    private fun emoteSearchFocusChangeListener(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            _emoteSearchFocused = true
            showKeyboardView()
        } else {
            _emoteSearchFocused = false
        }
    }


    fun setStatus(status: LOADINGSTATUS) {
        emotesView.setStatus(status)
    }

    private fun handleBackBtn(backBtn: View) {
        // backBtn MUST be visible if this fun is called
        if (emoteSearchFocused) {
            binding.emoteSearch.getSearchAutoComplete().clearFocus()
        } else if (binding.emotesView.isVisible) {
            showKeyboardView()
        }

        if (binding.keyboardView.isVisible) backBtn.isVisible = false
    }

    fun showEmotesView() {
        binding.backBtn.isVisible = true
        binding.emotesView.isVisible = true
        binding.keyboardView.isVisible = false
        binding.emoteSearch.getSearchAutoComplete().clearFocus()
    }

    fun showKeyboardView() {
        binding.emotesView.isVisible = false
        binding.keyboardView.isVisible = true
        binding.backBtn.isVisible = true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        this.onQueryTextChange(query)
        binding.emoteSearch.getSearchAutoComplete().clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        binding.emotesView.homeFragmentAdapter.filter.filter(newText)
        return false
    }

    fun submitQuery() {
//        binding.emoteSearch.setQuery(binding.emoteSearch.query, true)
        showEmotesView()
    }

}