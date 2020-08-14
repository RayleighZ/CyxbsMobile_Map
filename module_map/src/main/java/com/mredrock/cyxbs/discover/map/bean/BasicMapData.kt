package com.mredrock.cyxbs.discover.map.bean

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 首页地图数据包括地图地址、建筑坐标等等
 */
class BasicMapData : Serializable {
    @SerializedName("hot_word")
    var hotWord: String? = null

    @SerializedName("place_list")
    var placeList: MutableList<Place>? = null

    @SerializedName("map_url")
    var mapUrl: String? = null

    @SerializedName("map_width")
    var mapWidth: Float = 0f

    @SerializedName("map_height")
    var mapHeight: Float = 0f

    @SerializedName("map_background_color")
    var mapBackgroundColor: String? = null

    @SerializedName("open_site")
    var zoomInId: Int = 1

    @SerializedName("picture_version")
    var mapTimeStamp: Long = 0
}

//目前只在全局数据PlaceData类使用
@Entity(primaryKeys = ["picture_version"], tableName = "mapdatas")
class MapData : Serializable {
    @SerializedName("map_url")
    var mapUrl: String? = null

    @SerializedName("map_width")
    var mapWidth: Float = 0f

    @SerializedName("map_height")
    var mapHeight: Float = 0f

    @SerializedName("map_background_color")
    var mapBackgroundColor: String = "#96ECBB"

    @SerializedName("open_site")
    var zoomInId: Int = 1

    @SerializedName("picture_version")
    var mapTimeStamp: Long = 0

    //""表示当前图片没有进行本地存储
    var localPath : String = ""
}