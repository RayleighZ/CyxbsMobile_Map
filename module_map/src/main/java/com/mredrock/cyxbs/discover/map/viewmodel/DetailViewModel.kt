package com.mredrock.cyxbs.discover.map.viewmodel

import android.content.Context
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.ObservableField
import com.google.android.material.chip.ChipGroup
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.map.adapter.DetailViewPageAdapter
import com.mredrock.cyxbs.discover.map.view.widget.CycleTextView
import org.jetbrains.anko.textColor
import java.lang.ref.WeakReference

/**
 * @date 2020-08-10
 * @author Sca RayleighZ
 */
class DetailViewModel : BaseViewModel() {

    public var placeName = ObservableField<String>()

    //动态添加标识
    fun setIcon(containerWR : WeakReference<LinearLayout>, iconNames : List<String>){
        if(containerWR.get() == null){
            LogUtils.d("DetailViewModel" , "容器空了")
            return
        }

        val container = containerWR.get()
        var isFirstTime = true
        for (iconName in iconNames){
            val cycleTextView = CycleTextView(BaseApp.context)
            cycleTextView.text = iconName
            cycleTextView.textColor = Color.GRAY
            cycleTextView.post{
                cycleTextView.width += 40
            }
            LogUtils.d("DetailViewModel" , "加进去了")
            if (!isFirstTime){
                val empty = TextView(BaseApp.context)
                empty.width = 10
                container?.addView(empty)
                LogUtils.d("DetailViewModel" , "加了占位")
            } else {
                isFirstTime = false
            }
            container?.addView(cycleTextView)
        }
    }

    fun setDetails(chipGroupWR : WeakReference<ChipGroup> , details : List<String>){
        if(chipGroupWR.get() == null)
            return
        val chipGroup = chipGroupWR.get()
        for (detail in details){
            val cycleTextView =  CycleTextView(BaseApp.context)
            cycleTextView.text = detail
            cycleTextView.textColor = Color.parseColor("#234780")
            cycleTextView.post{
                cycleTextView.width += 40
            }
            chipGroup?.addView(cycleTextView)
        }
    }

    fun setDetailPic(vpAdapter : DetailViewPageAdapter , listOfPicUrls : List<String>){
        vpAdapter.listOfImageUrls = listOfPicUrls
        vpAdapter.notifyDataSetChanged()
    }
}