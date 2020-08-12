package com.mredrock.cyxbs.discover.map.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.map.R

class MaxHeightRecyclerView : RecyclerView {
    private var mMaxHeight = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initialize(context: Context, attrs: AttributeSet?) {
        val arr: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.map_MaxHeightRecyclerView)
        mMaxHeight = arr.getLayoutDimension(R.styleable.map_MaxHeightRecyclerView_map_maxHeight, mMaxHeight)
        arr.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec2 = heightMeasureSpec
        if (mMaxHeight > 0) {
            heightMeasureSpec2 = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec2)
    }
}