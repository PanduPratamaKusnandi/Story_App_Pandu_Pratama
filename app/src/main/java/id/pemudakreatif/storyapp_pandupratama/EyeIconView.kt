package id.pemudakreatif.storyapp_pandupratama.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import id.pemudakreatif.storyapp_pandupratama.R

class EyeIconView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint: Paint = Paint()
    private var eyeOpenDrawable: Drawable? = null
    private var eyeClosedDrawable: Drawable? = null
    private var isPasswordVisible: Boolean = false
    private var onClickListener: OnClickListener? = null

    init {
        eyeOpenDrawable = ContextCompat.getDrawable(context, R.drawable.mata)
        eyeClosedDrawable = ContextCompat.getDrawable(context, R.drawable.matacoret)

        paint.isAntiAlias = true
        setOnClickListener { onClickListener?.onClick() }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val drawable = if (isPasswordVisible) eyeOpenDrawable else eyeClosedDrawable
        drawable?.setBounds(0, 0, width, height)
        drawable?.draw(canvas)
    }

    fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        invalidate()
    }

    fun setOnClickListener(listener: OnClickListener) {
        onClickListener = listener
    }

    interface OnClickListener {
        fun onClick()
    }
}
