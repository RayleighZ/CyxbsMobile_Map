package com.mredrock.cyxbs.discover.map.model

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import okhttp3.ResponseBody
import java.io.File
import java.io.RandomAccessFile


class MapModel {
    companion object {
        const val TAG = "MapModel"
    }

    fun saveFile(responseBody: ResponseBody, filePath: String) {
        if (ContextCompat.checkSelfPermission(
                        BaseApp.context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                        BaseApp.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            CyxbsToast.makeText(BaseApp.context, "无存储权限，操作失败", Toast.LENGTH_LONG).show()
            return
        }
        var downloadByte: Long = 0
        val file = File(filePath)
        if (file.exists() && file.isFile) {
            file.delete()
        }
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        val buffer = ByteArray(1024 * 4)
        val randomAccessFile = RandomAccessFile(file, "rwd")
        val tempFileLen: Long = file.length()
        randomAccessFile.seek(tempFileLen)
        while (true) {
            val len: Int = responseBody.byteStream().read(buffer)
            if (len == -1) {
                break
            }
            randomAccessFile.write(buffer, 0, len)
            downloadByte += len.toLong()
        }
        randomAccessFile.close()
    }

}