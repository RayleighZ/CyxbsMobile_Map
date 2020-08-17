package com.mredrock.cyxbs.discover.map.bean

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 用户收藏
 */
@Entity(primaryKeys = ["placeId"], tableName = "favorites")
class FavoritePlace : Serializable {
    @SerializedName("place_id")
    var placeId: List<Int>? = null
}