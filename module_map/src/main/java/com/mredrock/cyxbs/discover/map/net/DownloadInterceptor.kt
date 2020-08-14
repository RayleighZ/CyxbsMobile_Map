package com.mredrock.cyxbs.discover.map.net

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.IOException
import org.jetbrains.annotations.NotNull

class ProgressInterceptor(private val progressListener: DownloadListener) : Interceptor {

    @NotNull
    @Throws(IOException::class)
    override fun intercept(@NotNull chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
        val body: ResponseBody? = originalResponse.body
        return originalResponse.newBuilder()
                .body(body?.let { ProgressResponseBody(chain.request().url.toUrl().toString(), it, progressListener) })
                .build()
    }

}