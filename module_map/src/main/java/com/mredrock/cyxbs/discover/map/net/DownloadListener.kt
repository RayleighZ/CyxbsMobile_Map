package com.mredrock.cyxbs.discover.map.net

interface DownloadListener {
    fun progress(url: String, bytesRead: Long, contentLength: Long, done: Boolean)
}