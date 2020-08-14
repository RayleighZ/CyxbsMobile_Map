package com.mredrock.cyxbs.discover.map.net

import androidx.databinding.ObservableField
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.map.bean.BasicMapData
import com.mredrock.cyxbs.discover.map.bean.ClassifyData
import com.mredrock.cyxbs.discover.map.bean.FavoritePlace
import com.mredrock.cyxbs.discover.map.bean.PlaceDetail
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*


interface ApiService {
    @GET("hot")
    fun getHot(): Observable<RedrockApiWrapper<String>>

    //获得收藏
    @GET("rockmap/collect")
    fun getCollect() : ObservableField<RedrockApiWrapper<FavoritePlace>>

    //搜索下方按钮
    @GET("button")
    fun getButton(): Observable<RedrockApiWrapper<ClassifyData>>

    @GET("basic")
    fun getBasicMapData(): Observable<RedrockApiWrapper<BasicMapData>>

    //搜索下方分类的内容
    @FormUrlEncoded
    @POST("searchtype")
    fun getClassifyInfoList(@Field("code") code: String): Observable<RedrockApiWrapper<List<Int>>>

    //详细地点
    @FormUrlEncoded
    @POST("detailsite")
    fun getDetail(@Field("place_id") placeId: Int): Observable<RedrockApiWrapper<PlaceDetail>>

    //返回服务器搜索的id
    // TODO: 2020/8/12 0012 返回Json没写
    @FormUrlEncoded
    @POST("addhot")
    fun addHot(@Field("id") placeId: Int): Observable<RedrockApiWrapper<PlaceDetail>>

    //增加收藏
    @FormUrlEncoded
    @PATCH("rockmap/addkeep")
    fun addKeep(@Field("place_id") placeId : Int) : Observable<RedrockApiStatus>

    //删除收藏
    @DELETE("rockmap/deletekeep")
    fun delKeep(@Field("place_id") placeId : Int) : Observable<RedrockApiStatus>

    /**
     * 下载文件
     */
    @Streaming
    @GET
    fun downloadMap(@Url url: String): Observable<ResponseBody>

}