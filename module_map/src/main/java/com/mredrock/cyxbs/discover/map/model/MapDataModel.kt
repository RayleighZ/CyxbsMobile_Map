package com.mredrock.cyxbs.discover.map.model

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.discover.map.bean.MapData
import com.mredrock.cyxbs.discover.map.config.PlaceData
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * @date 2020-08-15
 * @author Sca RayleighZ
 */
class MapDataModel {
    companion object{

        const val SP_NAME = "model_map_map_info"
        fun saveMapData(){
            BaseApp.context.sharedPreferences(SP_NAME).editor {
                putString("map_url" , PlaceData.mapData.mapUrl)
                putFloat("map_width" , PlaceData.mapData.mapWidth)
                putFloat("map_height" , PlaceData.mapData.mapHeight)
                putString("map_background_color" , PlaceData.mapData.mapBackgroundColor)
                putInt("open_site" ,PlaceData.mapData.zoomInId)
                putLong("picture_version" , PlaceData.mapData.mapTimeStamp)
            }
        }

        fun loadMapData(){
            val sp =
            BaseApp.context.sharedPreferences(SP_NAME)
            PlaceData.mapData.mapUrl = sp.getString("map_url" , "")
            PlaceData.mapData.mapWidth = sp.getFloat("map_width" , 0F)
            PlaceData.mapData.mapHeight = sp.getFloat("map_height" , 0F)
            PlaceData.mapData.mapBackgroundColor = sp.getString("map_background_color" , null)
            PlaceData.mapData.zoomInId = sp.getInt("open_site" , 1)
            PlaceData.mapData.mapTimeStamp = sp.getLong("picture_version" , 0)
        }
    }
}