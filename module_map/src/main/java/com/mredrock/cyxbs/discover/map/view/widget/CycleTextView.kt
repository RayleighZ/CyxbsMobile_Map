package com.mredrock.cyxbs.discover.map.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.internal.ContextUtils
import org.jetbrains.anko.textColor

/**
 * @date 2020-08-08
 * @author Sca RayleighZ
 * 带圆角外框的TextView
 */
internal class CycleTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(
            context,
            attrs
    )


    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    companion object{
        const val DEFAULT_WIDTH = 4//默认边线宽度，单位为pt
    }

    private val paint = Paint()
    /*private val rectFToDrawLeft = RectF(0F , 0F ,  9F , 9F)
    private val rectFToDrawRight = RectF(0F , 0F ,  9F , 9F)*/

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null)
            return
        paint.color = currentTextColor
        paint.strokeWidth = DEFAULT_WIDTH.toFloat()
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        gravity = Gravity.CENTER
//        canvas.drawLine(HALF_RADIUS.toFloat() , 0F, (measuredWidth- HALF_RADIUS).toFloat() , 0F ,paint)
//        canvas.drawLine(HALF_RADIUS.toFloat() , 2* HALF_RADIUS.toFloat(), (measuredWidth- HALF_RADIUS).toFloat() , 2* HALF_RADIUS.toFloat(), paint)
        canvas.drawRoundRect(2F , 2F , measuredWidth.toFloat() - 2, measuredHeight.toFloat() - 2, measuredHeight.toFloat()/2 , measuredHeight.toFloat()/2 , paint)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec  , heightMeasureSpec)
    }
}