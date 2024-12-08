package id.pemudakreatif.storyapp_pandupratama.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import id.pemudakreatif.storyapp_pandupratama.R

class CustomPasswordEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    private val errorText = "Kata Sandi minimal 8 karakter"
    private val normalBorderColor = "#2196F3"
    private val errorBorderColor = "#FF0000"
    private val validBorderColor = "#4CAF50"

    init {
        this.transformationMethod = PasswordTransformationMethod.getInstance()

        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()

                if (password.length < 8) {
                    this@CustomPasswordEditText.setError(errorText)
                    changeBorderColor(errorBorderColor)
                } else {
                    changeBorderColor(validBorderColor)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        setDefaultBackground()
    }

    private fun changeBorderColor(colorHex: String) {
        val background = background as GradientDrawable
        background.setStroke(2, Color.parseColor(colorHex))
    }

    private fun setDefaultBackground() {
        val drawable = context.getDrawable(R.drawable.edittext_background) as GradientDrawable
        background = drawable
    }
}
