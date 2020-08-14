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
                val baos = ByteArrayOutputStream()
                try {
                    val oos = ObjectOutputStream(baos)
                    oos.writeObject(PlaceData.mapData)
                    putString("mapData" ,String(baos.toByteArray()))
                } catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }

        fun loadMapData(){
            val baseString = BaseApp.context.sharedPreferences(SP_NAME).getString("mapData" , "")
            if (baseString == "")
                return
            try {
                val bais = baseString.byteInputStream()
                val bis = ObjectInputStream(bais)
                PlaceData.mapData = bis.readObject() as MapData
            } catch (e : java.lang.Exception){
                e.printStackTrace()
            }
        }
    }
}