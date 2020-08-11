package com.mredrock.cyxbs.discover.map.bean

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["placeId"], tableName = "places")
class Place {
    @SerializedName(value = "placeId")
    var placeId: Int = 0//本地数据库存储的ID标识，此处数据类型有待商榷
    var placeName: String? = null
    var buildingX: Int = 0
    var buildingY: Int = 0
    var buildingR: Int = 0
    var tagX: Int = 0
    var tagY: Int = 0
    var tagR: Int = 0
}