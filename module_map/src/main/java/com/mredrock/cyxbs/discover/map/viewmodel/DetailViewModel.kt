package com.mredrock.cyxbs.discover.map.viewmodel

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.ObservableField
import com.google.android.material.chip.ChipGroup
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BuildConfig
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
import com.mredrock.cyxbs.discover.map.net.ApiService
import com.mredrock.cyxbs.discover.map.util.TypeFaceUtil
import com.mredrock.cyxbs.discover.map.view.widget.CycleTextView
import org.jetbrains.anko.textColor
import java.lang.ref.WeakReference
import com.mredrock.cyxbs.discover.map.view.adapter.DetailViewPageAdapter
import com.mredrock.cyxbs.discover.map.view.fragment.DetailFragment
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @date 2020-08-10
 * @author Sca RayleighZ
 */
class DetailViewModel : BaseViewModel() {

    var placeName = ObservableField<String>()
    lateinit var curPlace : Place

    //动态添加标识
    fun setIcon(containerWR: WeakReference<LinearLayout>, iconNames: List<String>) {
        if (containerWR.get() == null) {
            LogUtils.d("DetailViewModel", "容器空了")
            return
        }

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

    fun setDetails(chipGroupWR: WeakReference<ChipGroup>, details: List<String>) {
        if (chipGroupWR.get() == null)
            return
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

    fun setDetailPic(vpAdapter: DetailViewPageAdapter, listOfPicUrls: List<String>) {
        vpAdapter.listOfImageUrls = listOfPicUrls
        vpAdapter.notifyDataSetChanged()
    }

    fun setDetail(llWR : WeakReference<LinearLayout>, chipGroupWR: WeakReference<ChipGroup>, vpAdapter: DetailViewPageAdapter){
        ApiGenerator.registerNetSettings(999, { builder -> retrofitConfigFun(builder) }
                , { builder -> okHttpClientConfigFun(builder) }, true)
        ApiGenerator.getApiService(999, ApiService::class.java)
                .getDetail(DetailFragment.placeId)
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    it.images?.let { it1 -> setDetailPic( vpAdapter, it1) }
                    it.tags?.let { it1 -> setDetails(chipGroupWR , it1) }
                    it.placeAttribute?.let { it1 -> setIcon(llWR , it1) }
                }.lifeCycle()
    }

    fun addKeep(ivWR : WeakReference<ImageView>){
        ApiGenerator.registerNetSettings(999, { builder -> retrofitConfigFun(builder) }
                , { builder -> okHttpClientConfigFun(builder) }, true)
        ApiGenerator.getApiService(999, ApiService::class.java)
                .addKeep(DetailFragment.placeId)
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    if (it.status == 200 && ivWR.get() != null){
                        ivWR.get()?.setImageResource(R.mipmap.map_ic_keeped)
                        curPlace.isCollected = true
                    }
                }.lifeCycle()
    }

    fun delKeep(ivWR: WeakReference<ImageView>){
        ApiGenerator.registerNetSettings(999, { builder -> retrofitConfigFun(builder) }
                , { builder -> okHttpClientConfigFun(builder) }, true)
        ApiGenerator.getApiService(999, ApiService::class.java)
                .delKeep(DetailFragment.placeId)
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    if (it.status == 200 && ivWR.get() != null){
                        ivWR.get()?.setImageResource(R.mipmap.map_ic_keep)
                        curPlace.isCollected = false
                    }
                }.lifeCycle()
    }

    private fun retrofitConfigFun(builder: Retrofit.Builder): Retrofit.Builder {
        builder.baseUrl("http://118.31.20.31:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        return builder
    }

    private fun okHttpClientConfigFun(builder: okhttp3.OkHttpClient.Builder): okhttp3.OkHttpClient.Builder {
        builder.run {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
        }
        return builder
    }
}