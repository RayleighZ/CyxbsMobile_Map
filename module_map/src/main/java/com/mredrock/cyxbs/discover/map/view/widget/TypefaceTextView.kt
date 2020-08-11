package com.mredrock.cyxbs.discover.map.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.util.TypeFaceUtil

class TypefaceTextView : AppCompatTextView {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.map_TypefaceTextView, 0, 0)
            val typefaceType = typedArray.getInt(R.styleable.map_TypefaceTextView_map_typeface, 0)
            typeface = getTypeface(typefaceType)
            typedArray.recycle()
        }
    }

    companion object {
        fun getTypeface(typefaceType: Int?) = when (typefaceType) {
            TypeFaceUtil.PFSCH_TYPEFACE -> TypeFaceUtil.getPFSCHTypeface()
            TypeFaceUtil.PFSCM_TYPEFACE -> TypeFaceUtil.getPFSCMTypeface()
            else -> Typeface.DEFAULT
        }
    }
}