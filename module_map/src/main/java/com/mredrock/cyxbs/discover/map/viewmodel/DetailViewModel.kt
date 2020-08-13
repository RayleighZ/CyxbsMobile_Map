package com.mredrock.cyxbs.discover.map.viewmodel

import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.ObservableField
import com.google.android.material.chip.ChipGroup
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.util.TypeFaceUtil
import com.mredrock.cyxbs.discover.map.view.widget.CycleTextView
import org.jetbrains.anko.textColor
import java.lang.ref.WeakReference
import com.mredrock.cyxbs.discover.map.view.adapter.DetailViewPageAdapter

/**
 * @date 2020-08-10
 * @author Sca RayleighZ
 */
class DetailViewModel : BaseViewModel() {

    var placeName = ObservableField<String>()

    //动态添加标识
    fun setIcon(containerWR: WeakReference<LinearLayout>, iconNames: List<String>) {
        if (containerWR.get() == null) {
            LogUtils.d("DetailViewModel", "容器空了")
            return
        }

        val container = containerWR.get()
        var isFirstTime = true
        for (iconName in iconNames) {
            val cycleTextView = CycleTextView(BaseApp.context)
            cycleTextView.text = iconName
            cycleTextView.textSize = 12f
            cycleTextView.setPadding(BaseApp.context.dp2px(6f),
                    BaseApp.context.dp2px(3f),
                    BaseApp.context.dp2px(6f),
                    BaseApp.context.dp2px(3f))
            cycleTextView.typeface = TypeFaceUtil.getPFSCMTypeface()
            cycleTextView.textColor = BaseApp.context.resources.getColor(R.color.map_fragment_detail_below_title_color)
//            cycleTextView.post {
//                cycleTextView.width += 40
//            }
            //LogUtils.d("DetailViewModel", "加进去了")
            if (!isFirstTime) {
                val empty = TextView(BaseApp.context)
                empty.width = BaseApp.context.dp2px(12f)
                container?.addView(empty)
                //LogUtils.d("DetailViewModel", "加了占位")
            } else {
                isFirstTime = false
            }
            container?.addView(cycleTextView)
        }
    }

    fun setDetails(chipGroupWR: WeakReference<ChipGroup>, details: List<String>) {
        if (chipGroupWR.get() == null)
            return
        val chipGroup = chipGroupWR.get()
        for (detail in details) {
            val cycleTextView = CycleTextView(BaseApp.context)
            cycleTextView.text = detail
            cycleTextView.textSize = 13f
            cycleTextView.setPadding(BaseApp.context.dp2px(12f),
                    BaseApp.context.dp2px(5f),
                    BaseApp.context.dp2px(12f),
                    BaseApp.context.dp2px(5f))
            cycleTextView.typeface = TypeFaceUtil.getPFSCMTypeface()
            cycleTextView.textColor = BaseApp.context.resources.getColor(R.color.map_activity_search_history_item_text_color)
            chipGroup?.addView(cycleTextView)
        }
    }

    fun setDetailPic(vpAdapter: DetailViewPageAdapter, listOfPicUrls: List<String>) {
        vpAdapter.listOfImageUrls = listOfPicUrls
        vpAdapter.notifyDataSetChanged()
    }
}