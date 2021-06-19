package io.github.transfusion.nitroless.ime

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener
import android.os.IBinder
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodSubtype
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import io.github.transfusion.nitroless.BuildConfig
import io.github.transfusion.nitroless.NitrolessApplication
import io.github.transfusion.nitroless.R
import io.github.transfusion.nitroless.data.NitrolessRepoEmoteModel
import io.github.transfusion.nitroless.storage.NitrolessRepo
import io.github.transfusion.nitroless.ui.home.HomeViewModel
import io.github.transfusion.nitroless.ui.home.HomeViewModelFactory
import io.github.transfusion.nitroless.ui.interfaces.EmoteClickedInterface


/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
class NitrolessInputMethodService : InputMethodService(), OnKeyboardActionListener,
    ViewModelStoreOwner, LifecycleOwner, LifecycleObserver, SavedStateRegistryOwner,
    EmoteClickedInterface {

    private var mInputMethodManager: InputMethodManager? = null
    private var mInputView: LatinKeyboardView? = null
    private val mComposing = StringBuilder()
    private var mLastDisplayWidth = 0
    private var mCapsLock = false
    private var mLastShiftTime: Long = 0
    private var mMetaState: Long = 0

    private var mSymbolsKeyboard: LatinKeyboard? = null
    private var mSymbolsShiftedKeyboard: LatinKeyboard? = null
    private var mQwertyKeyboard: LatinKeyboard? = null
    private var mCurKeyboard: LatinKeyboard? = null
    private var wordSeparators: String? = null

    private lateinit var nitrolessMainKeyboardView: NitrolessMainKeyboardView

    private lateinit var emoteSearchInputConnection: InputConnection

//    private var keyboardDragDelegate: KeyboardDragDelegate? = null

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    override fun onCreate() {
        super.onCreate()
        savedStateRegistry.performRestore(null)
        handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        mInputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        wordSeparators = resources.getString(R.string.word_separators)
//        keyboardDragDelegate = KeyboardDragDelegate(this, window.window)

        lifecycle.addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    /* @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun started() {} */

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    override fun onInitializeInterface() {
        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            val displayWidth = maxWidth
            if (displayWidth == mLastDisplayWidth) {
                return
            }
            mLastDisplayWidth = displayWidth
        }
        mQwertyKeyboard = LatinKeyboard(this, R.xml.qwerty)
        mSymbolsKeyboard = LatinKeyboard(this, R.xml.symbols)
        mSymbolsShiftedKeyboard = LatinKeyboard(this, R.xml.symbols_shift)
    }

    lateinit var homeViewModel: HomeViewModel

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    override fun onCreateInputView(): View {
        /*val keyboardParent = layoutInflater.inflate(
            R.layout.input, null
        ) as LinearLayout
        val handle = keyboardParent.findViewById<Button>(R.id.handle)*/
        /*handle.setOnTouchListener { view, motionEvent ->
            keyboardDragDelegate.onTouch(
                view,
                motionEvent
            )
        }*/
        /*mInputView = keyboardParent.findViewById<View>(R.id.keyboard) as LatinKeyboardView
        mInputView!!.setOnKeyboardActionListener(this)
        mInputView!!.isPreviewEnabled = false
        setLatinKeyboard(mQwertyKeyboard)
        return keyboardParent*/

        handleLifecycleEvent(Lifecycle.Event.ON_START)
        val app = (application as NitrolessApplication)
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(app.repository, app.recentlyUsedEmoteRepository)
        ).get(HomeViewModel::class.java)

        nitrolessMainKeyboardView = NitrolessMainKeyboardView(this)
        ViewTreeLifecycleOwner.set(nitrolessMainKeyboardView, this)
        ViewTreeViewModelStoreOwner.set(nitrolessMainKeyboardView, this)
        ViewTreeSavedStateRegistryOwner.set(nitrolessMainKeyboardView, this)

        emoteSearchInputConnection =
            CustomInputConnection(nitrolessMainKeyboardView.emoteSearch.getSearchAutoComplete())

        homeViewModel.status.observe(owner = this) {
            Log.d(javaClass.name, it.toString())
            nitrolessMainKeyboardView.setStatus(it)
        }

        mInputView = nitrolessMainKeyboardView.inputView
        mInputView!!.setOnKeyboardActionListener(this)
//        mInputView!!.isPreviewEnabled = false
        setLatinKeyboard(mQwertyKeyboard)
        return nitrolessMainKeyboardView
    }

    private val activeInputConnection: InputConnection?
        get() {
            return if (nitrolessMainKeyboardView.emoteSearchFocused) {
                emoteSearchInputConnection
            } else {
                currentInputConnection
            }
        }

    fun onEmoteClicked(
        nitrolessRepo: NitrolessRepo,
        path: String,
        emote: NitrolessRepoEmoteModel,
    ) {
        Log.d(javaClass.name, "ime emote click ${emote.name}")
    }

    private fun setLatinKeyboard(nextKeyboard: LatinKeyboard?) {
        val shouldSupportLanguageSwitchKey =
            mInputMethodManager!!.shouldOfferSwitchingToNextInputMethod(
                token
            )
        nextKeyboard!!.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey)
        mInputView!!.keyboard = nextKeyboard
    }

    /*override fun onConfigureWindow(win: Window, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        val params = window.window!!.attributes
        params.y = 200
        params.x = 0
        params.width = 400
        Log.d(
            TAG,
            "onConfigureWindow() called with: win = [$win], isFullscreen = [$isFullscreen], isCandidatesOnly = [$isCandidatesOnly]"
        )
        window.window!!.attributes = params
    }*/

    /*override fun onComputeInsets(outInsets: Insets) {
        outInsets.contentTopInsets = mInputView.height + navBarHeight + window.window!!
            .attributes.y

        // outInsets.visibleTopInsets =  getNavBarHeight();
    }*/

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.co
     */
    override fun onStartInput(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInput(attribute, restarting)

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0)
        if (!restarting) {
            // Clear shift states.
            mMetaState = 0
        }
        when (attribute.inputType and InputType.TYPE_MASK_CLASS) {
            InputType.TYPE_CLASS_NUMBER, InputType.TYPE_CLASS_DATETIME ->                 // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mSymbolsKeyboard
            InputType.TYPE_CLASS_PHONE ->                 // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mSymbolsKeyboard
            InputType.TYPE_CLASS_TEXT -> {
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mCurKeyboard = mQwertyKeyboard

                // We now look for a few special variations of text that will
                // modify our behavior.
                val variation = attribute.inputType and InputType.TYPE_MASK_VARIATION
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                    variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                }
                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS || variation == InputType.TYPE_TEXT_VARIATION_URI || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                }
                if (attribute.inputType and InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                updateShiftKeyState(attribute)
            }
            else -> {
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mCurKeyboard = mQwertyKeyboard
                updateShiftKeyState(attribute)
            }
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard!!.setImeOptions(resources, attribute.imeOptions)
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    override fun onFinishInput() {
        super.onFinishInput()

        // Clear current composing text and candidates.
        mComposing.setLength(0)

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false)
        mCurKeyboard = mQwertyKeyboard
        if (mInputView != null) {
            mInputView!!.closing()
        }
    }

    override fun onStartInputView(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInputView(attribute, restarting)
        // Apply the selected keyboard to the input view.
        setLatinKeyboard(mCurKeyboard)
        mInputView!!.closing()
        val subtype = mInputMethodManager!!.currentInputMethodSubtype
        mInputView!!.setSubtypeOnSpaceKey()
    }

    public override fun onCurrentInputMethodSubtypeChanged(subtype: InputMethodSubtype) {
        mInputView!!.setSubtypeOnSpaceKey()
    }

    /*private val navBarHeight: Int
        get() {
            val resources = resources
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else 0
        }*/

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int
    ) {
        super.onUpdateSelection(
            oldSelStart, oldSelEnd, newSelStart, newSelEnd,
            candidatesStart, candidatesEnd
        )

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.isNotEmpty() && (newSelStart != candidatesEnd
                    || newSelEnd != candidatesEnd)
        ) {
            mComposing.setLength(0)
            val ic = currentInputConnection
            ic?.finishComposingText()
        }
    }

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    /*private fun translateKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        mMetaState = MetaKeyKeyListener.handleKeyDown(
            mMetaState,
            keyCode, event
        )
        var c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState))
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState)
        val ic = currentInputConnection
        if (c == 0 || ic == null) {
            return false
        }
        var dead = false
        if (c and KeyCharacterMap.COMBINING_ACCENT != 0) {
            dead = true
            c = c and KeyCharacterMap.COMBINING_ACCENT_MASK
        }
        if (mComposing.isNotEmpty()) {
            val accent = mComposing[mComposing.length - 1]
            val composed = KeyEvent.getDeadChar(accent.toInt(), c)
            if (composed != 0) {
                c = composed
                mComposing.setLength(mComposing.length - 1)
            }
        }
        onKey(c, intArrayOf())
        return true
    }*/

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.d(javaClass.name, "onKeyDown $keyCode")
        when (keyCode) {
            KeyEvent.KEYCODE_BACK ->                 // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.repeatCount == 0 && mInputView != null) {
                    if (mInputView!!.handleBack()) {
                        return true
                    }
                }
            KeyEvent.KEYCODE_DEL ->
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
//                if (mComposing.isNotEmpty())
            {
                onKey(Keyboard.KEYCODE_DELETE, intArrayOf())
                return true
            }
            KeyEvent.KEYCODE_ENTER ->                 // Let the underlying text editor always handle these.
            {
                return handleReturn()
            }
            else -> {
                onKey(event.unicodeChar, intArrayOf())
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        return super.onKeyUp(keyCode, event)
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private fun commitTyped(inputConnection: InputConnection) {
        if (mComposing.isNotEmpty()) {
            inputConnection.commitText(mComposing, mComposing.length)
            mComposing.setLength(0)
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private fun updateShiftKeyState(attr: EditorInfo?) {
        if (attr != null && mInputView != null && mQwertyKeyboard == mInputView!!.keyboard) {
            var caps = 0
            val ei = currentInputEditorInfo
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = currentInputConnection.getCursorCapsMode(attr.inputType)
            }
            mInputView!!.isShifted = mCapsLock || caps != 0
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private fun isAlphabet(code: Int): Boolean {
        return Character.isLetter(code)
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private fun keyDownUp(keyEventCode: Int) {
        activeInputConnection?.sendKeyEvent(
            KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode)
        )
        activeInputConnection?.sendKeyEvent(
            KeyEvent(KeyEvent.ACTION_UP, keyEventCode)
        )
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private fun sendKey(keyCode: Int) {
        when (keyCode.toChar()) {
            '\n' -> keyDownUp(KeyEvent.KEYCODE_ENTER)
            else -> if (keyCode >= '0'.toInt() && keyCode <= '9'.toInt()) {
                keyDownUp(keyCode - '0'.toInt() + KeyEvent.KEYCODE_0)
            } else {
                activeInputConnection?.commitText(keyCode.toChar().toString(), 1)
            }
        }
    }

    // Implementation of KeyboardViewListener
    override fun onKey(primaryCode: Int, keyCodes: IntArray) {
        if (BuildConfig.DEBUG)
            Log.d("Test", "KEYCODE: $primaryCode")
        when {
            // should fall through to the isWordSeparator check below if
            // emotesView is not focused
            primaryCode == LatinKeyboardView.KEYCODE_RETURN && handleReturn() -> {
            }
            isWordSeparator(primaryCode) -> {
                // Handle separator
                if (mComposing.isNotEmpty()) {
                    commitTyped(currentInputConnection)
                }
                sendKey(primaryCode)
                updateShiftKeyState(currentInputEditorInfo)
            }
            primaryCode == Keyboard.KEYCODE_DELETE -> {
                handleBackspace()
            }
            primaryCode == Keyboard.KEYCODE_SHIFT -> {
                handleShift()
            }
            primaryCode == Keyboard.KEYCODE_CANCEL -> {
                handleClose()
            }
            primaryCode == LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH -> {
                handleLastInputMethod()
            }
            primaryCode == LatinKeyboardView.KEYCODE_SHOW_LANGUAGE_PICKER -> {
                handleLanguageSwitch()
            }
            primaryCode == LatinKeyboardView.KEYCODE_OPTIONS -> {
                // Show a menu or somethin'
            }
            primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                    && mInputView != null -> {
                val current = mInputView!!.keyboard
                if (current === mSymbolsKeyboard || current === mSymbolsShiftedKeyboard) {
                    setLatinKeyboard(mQwertyKeyboard)
                } else {
                    setLatinKeyboard(mSymbolsKeyboard)
                    mSymbolsKeyboard!!.isShifted = false
                }
            }
            else -> {
                handleCharacter(primaryCode, keyCodes)
            }
        }
    }

    override fun onText(text: CharSequence) {
        val ic = currentInputConnection ?: return
        ic.beginBatchEdit()
        if (mComposing.isNotEmpty()) {
            commitTyped(ic)
        }
        ic.commitText(text, 0)
        ic.endBatchEdit()
        updateShiftKeyState(currentInputEditorInfo)
    }

    private fun handleBackspace() {
        /*val length = mComposing.length
        when {
            length > 1 -> {
                mComposing.delete(length - 1, length)
                activeInputConnection?.setComposingText(mComposing, 1)
            }
            length > 0 -> {
                mComposing.setLength(0)
                activeInputConnection?.commitText("", 0)
            }
            else -> {
                keyDownUp(KeyEvent.KEYCODE_DEL)
            }
        }*/

        val selectedText: CharSequence? = activeInputConnection?.getSelectedText(0)
        if (TextUtils.isEmpty(selectedText)) {
            // no selection, so delete previous character
            activeInputConnection?.deleteSurroundingText(1, 0)
        } else {
            // delete the selection
            activeInputConnection?.commitText("", 1)
        }
        updateShiftKeyState(currentInputEditorInfo)
    }

    private fun handleShift() {
        if (mInputView == null) {
            return
        }
        val currentKeyboard = mInputView!!.keyboard
        if (mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock()
            mInputView!!.isShifted = mCapsLock || !mInputView!!.isShifted
        } else if (currentKeyboard === mSymbolsKeyboard) {
            mSymbolsKeyboard!!.isShifted = true
            setLatinKeyboard(mSymbolsShiftedKeyboard)
            mSymbolsShiftedKeyboard!!.isShifted = true
        } else if (currentKeyboard === mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard!!.isShifted = false
            setLatinKeyboard(mSymbolsKeyboard)
            mSymbolsKeyboard!!.isShifted = false
        }
    }

    private fun handleCharacter(primaryCode: Int, keyCodes: IntArray) {
        var primaryCode = primaryCode
        if (isInputViewShown) {
            if (mInputView!!.isShifted) {
                primaryCode = Character.toUpperCase(primaryCode)
            }
        }
        activeInputConnection?.commitText(primaryCode.toChar().toString(), 1)
    }

    private fun handleClose() {
        /*commitTyped(currentInputConnection)
        requestHideSelf(0)
        mInputView!!.closing()*/
        nitrolessMainKeyboardView.showEmotesView()
    }

    private fun handleReturn(): Boolean {
        if (nitrolessMainKeyboardView.emoteSearchFocused) {
            nitrolessMainKeyboardView.submitQuery()
            return true
        }
        return false
    }

    private val token: IBinder?
        get() {
            val dialog = window ?: return null
            val window = dialog.window ?: return null
            return window.attributes.token
        }

    private fun handleLastInputMethod() {
        mInputMethodManager!!.switchToLastInputMethod(token /*  false onlyCurrentIme */)
    }

    private fun handleLanguageSwitch() {
        mInputMethodManager?.showInputMethodPicker()
    }

    private fun checkToggleCapsLock() {
        val now = System.currentTimeMillis()
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock
            mLastShiftTime = 0
        } else {
            mLastShiftTime = now
        }
    }

    fun isWordSeparator(code: Int): Boolean {
        val separators = wordSeparators
        if (separators != null) {
            return separators.contains(code.toChar().toString())
        }
        return false
    }

    override fun swipeRight() {
        Log.d("SoftKeyboard", "Swipe right")
    }

    override fun swipeLeft() {
        Log.d("SoftKeyboard", "Swipe left")
        handleBackspace()
    }

    override fun swipeDown() {
        handleClose()
    }

    override fun swipeUp() {}
    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}

    companion object {
        private const val DEBUG = false

        /**
         * This boolean indicates the optional example code for performing
         * processing of hard keys in addition to regular text generation
         * from on-screen interaction.  It would be used for input methods that
         * perform language translations (such as converting text entered on
         * a QWERTY keyboard to Chinese), but may not be used for input methods
         * that are primarily intended to be used for on-screen text entry.
         */
        const val PROCESS_HARD_KEYS = true
        private const val TAG = "SoftKeyboard"
        private const val NOT_A_LENGTH = -1
    }

    private val store = ViewModelStore()
    override fun getViewModelStore(): ViewModelStore = store

    //    Lifecycle Methods
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private fun handleLifecycleEvent(event: Lifecycle.Event) =
        lifecycleRegistry.handleLifecycleEvent(event)

    //    SavedStateRegistry Methods
    private val savedStateRegistry = SavedStateRegistryController.create(this)
    override fun getSavedStateRegistry(): SavedStateRegistry = savedStateRegistry.savedStateRegistry
}
