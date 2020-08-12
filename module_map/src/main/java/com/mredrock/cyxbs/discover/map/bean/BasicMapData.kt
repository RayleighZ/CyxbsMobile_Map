package com.mredrock.cyxbs.discover.map.bean

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
    var mapBackgroundColor: String = "#96ECBB"

    @SerializedName("id")
    var zoomInId: Int = 1
}

//目前只在全局数据PlaceData类使用
class MapData : Serializable {
    @SerializedName("map_url")
    var mapUrl: String? = null

    @SerializedName("map_width")
    var mapWidth: Float = 0f

    @SerializedName("map_height")
    var mapHeight: Float = 0f

    @SerializedName("map_background_color")
    var mapBackgroundColor: String = "#96ECBB"

    var zoomInId: Int = 1
}