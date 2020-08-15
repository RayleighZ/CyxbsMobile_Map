package com.mredrock.cyxbs.discover.map.viewmodel

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.map.bean.BasicMapData
import com.mredrock.cyxbs.discover.map.bean.ClassifyData
import com.mredrock.cyxbs.discover.map.bean.FavoritePlace
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.model.MapModel
import com.mredrock.cyxbs.discover.map.net.ApiService
import com.mredrock.cyxbs.discover.map.net.DownloadListener
import com.mredrock.cyxbs.discover.map.net.ProgressInterceptor
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
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
    companion object {
        const val TAG = "MapViewModel"
    }

    var mDisposable: Disposable? = null          //用于取消下载地图
    val model = MapModel()
    var mClassify = MutableLiveData<ClassifyData>()
    var mHot = MutableLiveData<String>()
    var mBasicMapData = MutableLiveData<BasicMapData>()
    var mDownloadProgress: MutableLiveData<Float> = MutableLiveData()
    var mMapPath: MutableLiveData<String> = MutableLiveData()
    var mCollect = MutableLiveData<List<FavoritePlace>>()

    fun showPinByType(check: String) {

    }

    fun getRetrofitConfig(builder: Retrofit.Builder): Retrofit.Builder {
        builder.baseUrl(END_POINT_REDROCK)
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

    fun getOkHttpClientDownloadConfig(builder: okhttp3.OkHttpClient.Builder): okhttp3.OkHttpClient.Builder {
        builder.run {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
            addInterceptor(ProgressInterceptor(object : DownloadListener {
                override fun progress(url: String, bytesRead: Long, contentLength: Long, done: Boolean) {
                    this@MapViewModel.mDownloadProgress.postValue((bytesRead.toDouble() / contentLength).toFloat())
                    if (done) {
                        this@MapViewModel.mMapPath.postValue(Environment.getExternalStorageDirectory().absolutePath + "/CQUPTMap/CQUPTMap.jpg")
                    }
                }
            }))
        }
        return builder
    }

    fun getClassify() {
        ApiGenerator.getApiService(ApiService::class.java)
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
        ApiGenerator.getApiService(ApiService::class.java)
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
        ApiGenerator.getApiService(ApiService::class.java)
                .getBasicMapData()
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    mBasicMapData.value = it
                }.lifeCycle()
    }

    fun getCollect() {
        ApiGenerator.getApiService(ApiService::class.java)
                .getCollect()
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    mCollect.value = it
                }.lifeCycle()
    }

    fun getMap() {
        ApiGenerator.registerNetSettings(20200815, { builder -> getRetrofitConfig(builder) }
                , { builder -> getOkHttpClientDownloadConfig(builder) }, true)
        ApiGenerator.getApiService(20200815, ApiService::class.java)
                .downloadMap(PlaceData.mapData.mapUrl ?: "")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext {
                    val path = Environment.getExternalStorageDirectory().absolutePath + "/CQUPTMap/CQUPTMap.jpg"
                    model.saveFile(it, path)
                }
                .doOnError { throwable ->
                    LogUtils.e(TAG, "accept on error: ${PlaceData.mapData.mapUrl}", throwable)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseBody?> {
                    override fun onSubscribe(d: Disposable) {
                        mDisposable = d
                    }

                    override fun onNext(t: ResponseBody) {
                    }

                    override fun onError(e: Throwable) {
                        LogUtils.e(TAG, "accept on error: ${e.message}")
                    }

                    override fun onComplete() {

                    }
                })

    }

    fun addHot(placeId: Int) {
        ApiGenerator.getApiService(ApiService::class.java)
                .addHot(placeId)
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    if (it.status == 200) {
                        LogUtils.d(TAG, "add hot success")
                    } else {
                        LogUtils.w(TAG, "add hot status is ${it.status}")
                    }
                }.lifeCycle()
    }
}