package io.github.transfusion.nitroless.ime

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.KeyboardView
import android.util.Log
import android.view.View
import io.github.transfusion.nitroless.R


class NitrolessInputMethodServiceOld : InputMethodService(), KeyboardView.OnKeyboardActionListener {


    private var mSymbolsKeyboard: LatinKeyboard? = null
    private var mSymbolsShiftedKeyboard: LatinKeyboard? = null
    private var mQwertyKeyboard: LatinKeyboard? = null

    private lateinit var nitrolessMainKeyboardView: NitrolessMainKeyboardView
    /*override fun onCreateInputView(): View {
        nitrolessMainKeyboardView = NitrolessMainKeyboardView(this)
        return nitrolessMainKeyboardView
    }*/

    override fun onInitializeInterface() {
        mQwertyKeyboard = LatinKeyboard(this, R.xml.qwerty)
        mSymbolsKeyboard = LatinKeyboard(this, R.xml.symbols)
        mSymbolsShiftedKeyboard = LatinKeyboard(this, R.xml.symbols_shift)
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onPress(primaryCode: Int) {
        Log.d(javaClass.name, "onPress $primaryCode")
    }

    override fun onRelease(primaryCode: Int) {
        Log.d(javaClass.name, "onRelease $primaryCode")
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        Log.d(javaClass.name, "onKey $primaryCode, ${keyCodes.toString()}")
    }

    override fun onText(text: CharSequence?) {
        Log.d(javaClass.name, "onText ${text.toString()}")
    }

    override fun swipeLeft() {
        Log.d(javaClass.name, "SWIPE LEFT")
    }

    override fun swipeRight() {
        Log.d(javaClass.name, "SWIPE RIGHT")
    }

    override fun swipeDown() {
        Log.d(javaClass.name, "SWIPE DOWN")
    }

    override fun swipeUp() {
        Log.d(javaClass.name, "SWIPE UP")
    }
}