package com.mredrock.cyxbs.discover.map.net

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager private constructor() {
    private val mRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASIC_URL) //设置网络请求的Url地址
            .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    fun <T> create(service: Class<T>): T? {
        return mRetrofit.create(service)
    }

    companion object {
        const val BASIC_URL = "http://118.31.20.31:8080/"

        val instance: RetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            //利用LazyThreadSafetyMode.SYNCHRONIZED参数来保证线程安全
            RetrofitManager()
        }
    }
}