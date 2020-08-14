package com.mredrock.cyxbs.discover.map.model

import okhttp3.ResponseBody
import java.io.File
import java.io.RandomAccessFile


class MapModel {
    companion object {
        const val TAG = "MapModel"
    }

    fun saveFile(responseBody: ResponseBody, filePath: String) {
        var downloadByte: Long = 0
        val file = File(filePath)
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