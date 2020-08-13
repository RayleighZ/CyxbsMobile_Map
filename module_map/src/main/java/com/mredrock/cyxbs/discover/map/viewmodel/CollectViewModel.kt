package com.mredrock.cyxbs.discover.map.viewmodel

import android.view.View
import android.widget.TextView
import androidx.databinding.ObservableField
import com.google.android.material.chip.ChipGroup
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.util.TypeFaceUtil
import com.mredrock.cyxbs.discover.map.view.widget.CycleTextView
import org.jetbrains.anko.textColor
import java.lang.ref.WeakReference

/**
 * @date 2020-08-12
 * @author Sca RayleighZ
 */
class CollectViewModel : BaseViewModel() , View.OnClickListener{
    val placeName = ObservableField<String>()
    val editTextCharNum = ObservableField<String>("4/8")
    val editTextString = ObservableField<String>("我的收藏")

    fun addRecommend(containerWR: WeakReference<ChipGroup>, recommends: List<String>) {
        if (containerWR.get() == null)
            return
        val container = containerWR.get()
        var isFirstTime = true
        for (recommend in recommends) {
            val cycleTextView = CycleTextView(BaseApp.context)
            cycleTextView.text = recommend
            cycleTextView.textSize = 12f
            cycleTextView.setPadding(BaseApp.context.dp2px(6f),
                    BaseApp.context.dp2px(3f),
                    BaseApp.context.dp2px(6f),
                    BaseApp.context.dp2px(3f))
            cycleTextView.typeface = TypeFaceUtil.getPFSCMTypeface()
            cycleTextView.textColor = BaseApp.context.resources.getColor(R.color.map_fragment_detail_below_title_color)
            if (!isFirstTime) {
                val empty = TextView(BaseApp.context)
                empty.width = BaseApp.context.dp2px(12f)
                container?.addView(empty)
            } else {
                isFirstTime = false
            }
            cycleTextView.setOnClickListener(this)
            container?.addView(cycleTextView)
        }
    }

    override fun onClick(p0: View?) {
        val textView = p0 as CycleTextView
        editTextString.set(textView.text.toString())
        editTextCharNum.set("${textView.text.length}/8")
    }
}