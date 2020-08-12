package com.mredrock.cyxbs.discover.map.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.map.bean.BasicMapData
import com.mredrock.cyxbs.discover.map.bean.ClassifyData
import com.mredrock.cyxbs.discover.map.net.ApiService
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @date 2020-08-08
 * @author Sca RayleighZ
 * edit by Wangtianqi
 */
class MapViewModel : BaseViewModel() {
    var mClassify = MutableLiveData<ClassifyData>()
    var mHot = MutableLiveData<String>()
    var mBasicMapData = MutableLiveData<BasicMapData>()

    fun showPinByType(check: String) {

    }

    fun retrofitConfigFun(builder: Retrofit.Builder): Retrofit.Builder {
        builder.baseUrl("http://118.31.20.31:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        return builder
    }

    fun okHttpClientConfigFun(builder: okhttp3.OkHttpClient.Builder): okhttp3.OkHttpClient.Builder {
        builder.run {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
        }
        return builder
    }

    fun getClassify() {
        ApiGenerator.registerNetSettings(999, { builder -> retrofitConfigFun(builder) }
                , { builder -> okHttpClientConfigFun(builder) }, true)
        ApiGenerator.getApiService(999, ApiService::class.java)
                .getButton()
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    mClassify.value = it
                }.lifeCycle()
    }

    fun getHot() {
        ApiGenerator.registerNetSettings(999, { builder -> retrofitConfigFun(builder) }
                , { builder -> okHttpClientConfigFun(builder) }, true)
        ApiGenerator.getApiService(999, ApiService::class.java)
                .getHot()
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    mHot.value = it
                }.lifeCycle()
    }

    fun getBasicMapData() {
        ApiGenerator.registerNetSettings(999, { builder -> retrofitConfigFun(builder) }
                , { builder -> okHttpClientConfigFun(builder) }, true)
        ApiGenerator.getApiService(999, ApiService::class.java)
                .getBasicMapData()
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    mBasicMapData.value = it
                }.lifeCycle()
    }
}