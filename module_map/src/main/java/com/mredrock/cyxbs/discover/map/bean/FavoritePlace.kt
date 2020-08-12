package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 用户收藏
 */
class FavoritePlace : Serializable {
    @SerializedName("place_nickname")
    var placeNickname: String? = null            //用户起的名字

    @SerializedName("place_id")
    var placeId: Int = 0
}