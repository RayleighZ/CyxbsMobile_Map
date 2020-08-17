package com.mredrock.cyxbs.discover.map.viewmodel

import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.component.CyxbsToast
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
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class ShowAllPicViewModel : BaseViewModel() {
    companion object {
        const val TAG = "ShowAllPicViewModel"
    }

    fun uploadPhoto(uris: List<Uri>, paths: List<String>, placeId: Int) {
        for (i: Int in uris.indices) {
            val file = File(paths[i])
            val requestFile: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            val fileFormat = paths[i].split('.')
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", System.currentTimeMillis().toString() + "." + fileFormat[fileFormat.size - 1], requestFile)
            LogUtils.e("---", "${System.currentTimeMillis().toString() + "." + fileFormat[fileFormat.size - 1]}")
            ApiGenerator.getApiService(ApiService::class.java)
                    .uploadPhoto(body, placeId)
                    .setSchedulers()
                    .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                    .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                    .safeSubscribeBy {
                        if (it.status != 200) {
                            CyxbsToast.makeText(BaseApp.context, "上传图片失败", Toast.LENGTH_SHORT).show()
                        }
                    }.lifeCycle()
        }
    }
}