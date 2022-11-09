package com.saeware.storyapp.view

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.saeware.storyapp.R

class PasswordEditText : AppCompatEditText {
    constructor(context: Context): super(context) { init() }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init() }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init() }

    private fun init() {
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = PasswordTransformationMethod.getInstance()
        setTextColor(ContextCompat.getColor(context, R.color.primary))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            setAutofillHints(AUTOFILL_HINT_PASSWORD)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length < 6)
                    error = context.getString(R.string.error_password)
            }
        })
    }
}