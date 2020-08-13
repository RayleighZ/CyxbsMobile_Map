package com.mredrock.cyxbs.discover.map.bean

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BuildingRectConverter {
    @TypeConverter
    fun getBuildingRectFromString(value: String): List<Place.BuildingRect>? {
        return Gson().fromJson<List<Place.BuildingRect>>(value, object : TypeToken<List<Place.BuildingRect>>() {

        }.type)
    }

    @TypeConverter
    fun storeBuildingRectToString(tasks: List<Place.BuildingRect>): String {
        return Gson().toJson(tasks)
    }
}