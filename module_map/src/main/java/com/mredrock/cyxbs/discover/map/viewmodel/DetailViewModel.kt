package com.mredrock.cyxbs.discover.map.viewmodel

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.ObservableField
import com.google.android.material.chip.ChipGroup
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.model.PlaceModel
import com.mredrock.cyxbs.discover.map.net.ApiService
import com.mredrock.cyxbs.discover.map.util.TypeFaceUtil
import com.mredrock.cyxbs.discover.map.view.adapter.DetailViewPageAdapter
import com.mredrock.cyxbs.discover.map.view.fragment.DetailFragment
import com.mredrock.cyxbs.discover.map.view.widget.CycleTextView
import org.jetbrains.anko.textColor
import java.lang.ref.WeakReference

/**
 * @date 2020-08-10
 * @author Sca RayleighZ
 */
class DetailViewModel : BaseViewModel() {

    var placeName = ObservableField<String>()
    lateinit var curPlace: Place
    var listOfPicUrls = ArrayList<String>()

    //动态添加标识
    private fun setIcon(containerWR: WeakReference<LinearLayout>, iconNames: List<String>) {
        if (containerWR.get() == null) {
            LogUtils.d("DetailViewModel", "容器空了")
            return
        }

        LogUtils.d("DetailViewModel_asdasdasdasdasdasdasdasd", iconNames[0])


        val container = containerWR.get()
        container?.removeAllViews()
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

    private fun setDetails(chipGroupWR: WeakReference<ChipGroup>, details: List<String>) {
        if (chipGroupWR.get() == null){
            LogUtils.d("DetailFragment", "暂无icon数据")
            return
        }

        val chipGroup = chipGroupWR.get()
        chipGroup?.removeAllViews()
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

    private fun setDetailPic(vpAdapter: DetailViewPageAdapter, listOfPicUrls: List<String>) {
        vpAdapter.listOfImageUrls.clear()
        vpAdapter.listOfImageUrls.addAll(listOfPicUrls)
        vpAdapter.notifyDataSetChanged()
    }

    fun setDetail(llWR: WeakReference<LinearLayout>, chipGroupWR: WeakReference<ChipGroup>, vpAdapter: DetailViewPageAdapter) {
        ApiGenerator.getApiService(ApiService::class.java)
                .getDetail(DetailFragment.placeId)
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    if (it == null)
                        return@safeSubscribeBy
                    if (it.images == null){
                        listOfPicUrls.clear()
                        setDetailPic(vpAdapter , listOf())
                    }
                    it.placeAttribute?.get(0)?.let { it1 -> LogUtils.d("detailVM1" , it1) }
                    if (it.images != null){
                        listOfPicUrls.clear()
                        listOfPicUrls.addAll(it.images!!)
                    }

                    if (it.tags == null){
                        setDetails(chipGroupWR, listOf())
                    }
                    it.images?.let { it1 -> setDetailPic(vpAdapter, it1) }
                    it.tags?.let { it1 -> setDetails(chipGroupWR, it1) }
                    it.placeAttribute?.get(0)?.let { it1 -> LogUtils.d("detailVM2" , it1) }
                    it.placeAttribute?.let { it1 -> setIcon(llWR, it1) }
                }.lifeCycle()
    }

    fun addKeep(ivWR: WeakReference<ImageView>) {
        ApiGenerator.getApiService(ApiService::class.java)
                .addKeep(DetailFragment.placeId)
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    if (it.status == 200 && ivWR.get() != null) {
                        ivWR.get()?.setImageResource(R.drawable.map_ic_collected)
                        curPlace.isCollected = true

                        PlaceModel.insertCollectPlace(false , curPlace)

                        for (i: Int in PlaceData.placeList.indices) {
                            if (PlaceData.placeList[i].placeId == curPlace.placeId) {
                                PlaceData.placeList[i].isCollected = true
                                PlaceData.collectPlaceList.add(curPlace)
                                break
                            }
                        }
                    }
                }.lifeCycle()
    }

    @SuppressLint("NewApi")
    fun delKeep(ivWR: WeakReference<ImageView>) {
        ApiGenerator.getApiService(ApiService::class.java)
                .delKeep(DetailFragment.placeId)
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    if (it.status == 200 && ivWR.get() != null) {
                        ivWR.get()?.setImageResource(R.drawable.map_ic_stared)
                        curPlace.isCollected = false

                        PlaceModel.delCollectPlace(false , DetailFragment.placeId)

                        for (i: Int in PlaceData.placeList.indices) {
                            if (PlaceData.placeList[i].placeId == curPlace.placeId) {
                                PlaceData.placeList[i].isCollected = false
                                PlaceData.collectPlaceList.removeIf { t -> t.placeId == curPlace.placeId }
                                break
                            }
                        }
                    }
                }.lifeCycle()
    }
}