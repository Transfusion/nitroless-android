package io.github.transfusion.nitroless.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import io.github.transfusion.nitroless.databinding.KeyboardMainBinding

class NitrolessInputMethodService : InputMethodService() {
    private var _binding: KeyboardMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateInputView(): View {
//        _binding = KeyboardMainBinding.inflate(layoutInflater)
        return NitrolessMainKeyboardView(this)
//        return super.onCreateInputView()
//        return binding.root
    }
}