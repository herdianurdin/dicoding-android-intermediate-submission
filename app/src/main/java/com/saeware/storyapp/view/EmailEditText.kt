package com.saeware.storyapp.view

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.saeware.storyapp.R

class EmailEditText : AppCompatEditText {
    constructor(context: Context): super(context) { init() }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init() }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init() }

    private fun init() {
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        setTextColor(ContextCompat.getColor(context, R.color.primary))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            setAutofillHints(AUTOFILL_HINT_EMAIL_ADDRESS)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s).matches())
                    error = context.getString(R.string.error_email)
            }
        })
    }
}