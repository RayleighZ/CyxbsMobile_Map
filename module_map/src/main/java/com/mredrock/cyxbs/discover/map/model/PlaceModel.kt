package com.mredrock.cyxbs.discover.map.model

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.database.PlaceDatabase

/**
 * 考虑到基本上没有需求单个的添加地点或者减少地点
 * 只提供存储和更新两个方法
 */
class PlaceModel {
    companion object {
        val INSTANCE: PlaceModel by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PlaceModel()
        }
    }

    //全部加载
    fun loadPlace(onLoaded : () ->Unit) {
        Thread{
            PlaceData.placeList.addAll(PlaceDatabase.getDataBase(BaseApp.context).getPlaceDao().queryAllPlaces())
            onLoaded()
        }.start()
    }

    //全部存储，由于索引为ID，所以如果不清除直接添加会报错
    fun saveAllPlace(onSaved : ()-> Unit){
        Thread{
            PlaceDatabase.getDataBase(BaseApp.context).getPlaceDao().deleteAllPlaces()
            PlaceDatabase.getDataBase(BaseApp.context).getPlaceDao().insertAllPlaces(PlaceData.placeList)
            onSaved()
        }.start()
    }
}


