package com.mredrock.cyxbs.discover.map.net

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager private constructor() {
    private val mRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl("") //设置网络请求的Url地址
            .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    fun <T> create(service: Class<T>): T? {
        return mRetrofit.create(service)
    }

    companion object {
        val instance: RetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            //利用LazyThreadSafetyMode.SYNCHRONIZED参数来保证线程安全
            RetrofitManager()
        }
    }
}