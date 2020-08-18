package com.mredrock.cyxbs.discover.map.viewmodel

import android.net.Uri
import android.widget.Toast
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.map.net.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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