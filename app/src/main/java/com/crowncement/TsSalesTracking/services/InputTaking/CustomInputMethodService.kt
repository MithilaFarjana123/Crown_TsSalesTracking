package com.crowncement.TsSalesTracking.services.InputTaking

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

class CustomInputMethodService: InputMethodService() {

    private var activeInputConnection: InputConnection? = null

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        activeInputConnection = currentInputConnection
    }

    override fun onFinishInput() {
        super.onFinishInput()
        activeInputConnection = null
    }

    private fun requestCursorAnchorInfoIfActive() {
        if (activeInputConnection != null) {
           // activeInputConnection?.requestCursorAnchorInfo(0)
        } else {
            Log.e("CustomInputMethodService", "InputConnection is inactive.")
        }
    }
}