package com.mredrock.cyxbs.discover.map.config

import com.mredrock.cyxbs.discover.map.bean.MapData
import com.mredrock.cyxbs.discover.map.bean.Place

object PlaceData {
    var placeList: MutableList<Place> = ArrayList()
    var collectPlaceList: MutableList<Place> = ArrayList()
    var searchHistoryList: MutableList<Place> = ArrayList()
    var mapData: MapData = MapData()
}