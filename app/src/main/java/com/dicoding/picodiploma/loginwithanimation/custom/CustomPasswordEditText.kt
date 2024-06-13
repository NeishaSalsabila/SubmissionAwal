package com.dicoding.picodiploma.loginwithanimation.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText

class CustomPasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs), View.OnTouchListener {

    init {
        setupTextChangedListener()
        setOnTouchListener(this)
        setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun setupTextChangedListener() {
        this.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.length < 8) {
                error = "Password tidak boleh kurang dari 8 karakter"
            } else {
                error = null
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }
}
