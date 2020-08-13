package com.mredrock.cyxbs.discover.map.model

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.database.PlaceDatabase
import com.mredrock.cyxbs.discover.map.net.RetrofitManager

class LocalProvider {

    companion object {
        val instance: LocalProvider by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LocalProvider()
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


