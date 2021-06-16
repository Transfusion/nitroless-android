package io.github.transfusion.nitroless.ime

import android.view.inputmethod.InputConnection

import android.text.style.SuggestionSpan

import android.text.Spanned

import android.os.Bundle

import android.view.inputmethod.ExtractedText

import android.view.inputmethod.ExtractedTextRequest

import android.view.inputmethod.CorrectionInfo

import android.widget.TextView

import android.view.inputmethod.CompletionInfo

import android.text.Editable
import android.text.method.KeyListener
import android.util.Log

import android.view.inputmethod.BaseInputConnection


class CustomInputConnection(private val mTextView: TextView?) :
    BaseInputConnection(mTextView, true) {
    // Keeps track of nested begin/end batch edit to ensure this connection always has a
    // balanced impact on its associated TextView.
    // A negative value means that this connection has been finished by the InputMethodManager.
    private var mBatchEditNesting = 0
    override fun getEditable(): Editable? {
        val tv = mTextView
        return tv?.editableText
    }

    override fun beginBatchEdit(): Boolean {
        synchronized(this) {
            if (mBatchEditNesting >= 0) {
                mTextView!!.beginBatchEdit()
                mBatchEditNesting++
                return true
            }
        }
        return false
    }

    override fun endBatchEdit(): Boolean {
        synchronized(this) {
            if (mBatchEditNesting > 0) {
                // When the connection is reset by the InputMethodManager and reportFinish
                // is called, some endBatchEdit calls may still be asynchronously received from the
                // IME. Do not take these into account, thus ensuring that this IC's final
                // contribution to mTextView's nested batch edit count is zero.
                mTextView!!.endBatchEdit()
                mBatchEditNesting--
                return true
            }
        }
        return false
    }

    //    @Override
    //    protected void reportFinish() {
    //        super.reportFinish();
    //
    //        synchronized(this) {
    //            while (mBatchEditNesting > 0) {
    //                endBatchEdit();
    //            }
    //            // Will prevent any further calls to begin or endBatchEdit
    //            mBatchEditNesting = -1;
    //        }
    //    }
    override fun clearMetaKeyStates(states: Int): Boolean {
        val content = editable ?: return false
        val kl: KeyListener? = mTextView!!.keyListener
        if (kl != null) {
            try {
                kl.clearMetaKeyState(mTextView, content, states)
            } catch (e: AbstractMethodError) {
                // This is an old listener that doesn't implement the
                // new method.
            }
        }
        return true
    }

    override fun commitCompletion(text: CompletionInfo): Boolean {
        if (DEBUG) Log.v(
            TAG,
            "commitCompletion $text"
        )
        mTextView!!.beginBatchEdit()
        mTextView.onCommitCompletion(text)
        mTextView.endBatchEdit()
        return true
    }

    /**
     * Calls the [TextView.onCommitCorrection] method of the associated TextView.
     */
    override fun commitCorrection(correctionInfo: CorrectionInfo): Boolean {
        if (DEBUG) Log.v(
            TAG,
            "commitCorrection$correctionInfo"
        )
        mTextView!!.beginBatchEdit()
        mTextView.onCommitCorrection(correctionInfo)
        mTextView.endBatchEdit()
        return true
    }

    override fun performEditorAction(actionCode: Int): Boolean {
        if (DEBUG) Log.v(
            TAG,
            "performEditorAction $actionCode"
        )
        mTextView!!.onEditorAction(actionCode)
        return true
    }

    override fun performContextMenuAction(id: Int): Boolean {
        if (DEBUG) Log.v(
            TAG,
            "performContextMenuAction $id"
        )
        mTextView!!.beginBatchEdit()
        mTextView.onTextContextMenuItem(id)
        mTextView.endBatchEdit()
        return true
    }

    override fun getExtractedText(request: ExtractedTextRequest, flags: Int): ExtractedText? {
        if (mTextView != null) {
            val et = ExtractedText()
            if (mTextView.extractText(request, et)) {
                if (flags and GET_EXTRACTED_TEXT_MONITOR != 0) {
//                    mTextView.setExtracting(request);
                }
                return et
            }
        }
        return null
    }

    override fun performPrivateCommand(action: String, data: Bundle): Boolean {
        mTextView!!.onPrivateIMECommand(action, data)
        return true
    }

    override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
        if (mTextView == null) {
            return super.commitText(text, newCursorPosition)
        }
        if (text is Spanned) {
            val spans = text.getSpans(
                0, text.length,
                SuggestionSpan::class.java
            )
            //            mIMM.registerSuggestionSpansForNotification(spans);
        }

//        mTextView.resetErrorChangedFlag();
        //        mTextView.hideErrorIfUnchanged();
        return super.commitText(text, newCursorPosition)
    }

    override fun requestCursorUpdates(cursorUpdateMode: Int): Boolean {
        if (DEBUG) Log.v(
            TAG,
            "requestUpdateCursorAnchorInfo $cursorUpdateMode"
        )

        // It is possible that any other bit is used as a valid flag in a future release.
        // We should reject the entire request in such a case.
        val KNOWN_FLAGS_MASK = CURSOR_UPDATE_IMMEDIATE or
                CURSOR_UPDATE_MONITOR
        val unknownFlags = cursorUpdateMode and KNOWN_FLAGS_MASK.inv()
        if (unknownFlags != 0) {
            if (DEBUG) {
                Log.d(
                    TAG, "Rejecting requestUpdateCursorAnchorInfo due to unknown flags." +
                            " cursorUpdateMode=" + cursorUpdateMode +
                            " unknownFlags=" + unknownFlags
                )
            }
            return false
        }
        return false

//        if (mIMM == null) {
//            // In this case, TYPE_CURSOR_ANCHOR_INFO is not handled.
//            // TODO: Return some notification code rather than false to indicate method that
//            // CursorAnchorInfo is temporarily unavailable.
//            return false;
//        }
//        mIMM.setUpdateCursorAnchorInfoMode(cursorUpdateMode);
//        if ((cursorUpdateMode & InputConnection.CURSOR_UPDATE_IMMEDIATE) != 0) {
//            if (mTextView == null) {
//                // In this case, FLAG_CURSOR_ANCHOR_INFO_IMMEDIATE is silently ignored.
//                // TODO: Return some notification code for the input method that indicates
//                // FLAG_CURSOR_ANCHOR_INFO_IMMEDIATE is ignored.
//            } else if (mTextView.isInLayout()) {
//                // In this case, the view hierarchy is currently undergoing a layout pass.
//                // IMM#updateCursorAnchorInfo is supposed to be called soon after the layout
//                // pass is finished.
//            } else {
//                // This will schedule a layout pass of the view tree, and the layout event
//                // eventually triggers IMM#updateCursorAnchorInfo.
//                mTextView.requestLayout();
//            }
//        }
//        return true;
    }

    companion object {
        private const val DEBUG = false
        private const val TAG = "CustomInputConnection"
    }
}