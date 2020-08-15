package com.mredrock.cyxbs.discover.map.model

import android.database.sqlite.SQLiteConstraintException
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.database.FavoriteDataBase
import com.mredrock.cyxbs.discover.map.database.HistoryDatabase
import com.mredrock.cyxbs.discover.map.database.PlaceDatabase

class PlaceModel {
    companion object {

       /* val databaseThread: Thread by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Thread()
        }*/

        //全部加载
        fun loadPlace(onLoaded : () ->Unit) {
            Thread {
                PlaceData.placeList.addAll(PlaceDatabase.getDataBase().getPlaceDao().queryAllPlaces())
                onLoaded()
            }.start()
        }

        //全部存储，由于索引为ID，所以如果不清除直接添加会报错
        fun saveAllPlace(onSaved : ()-> Unit){
            Thread {
                PlaceDatabase.getDataBase().getPlaceDao().deleteAllPlaces()
                PlaceDatabase.getDataBase().getPlaceDao().insertAllPlaces(PlaceData.placeList)
                onSaved()
            }.start()
        }

        fun insertCollectPlace(place : Place){
            Thread {
                try {
                    FavoriteDataBase.getDataBase().getFavoriteDao().insertPlace(place)
                } catch ( e : SQLiteConstraintException){
                    e.printStackTrace()
                }
            }.start()
        }

        fun delCollectPlace(placeId: Int){
            Thread{
                FavoriteDataBase.getDataBase().getFavoriteDao().deletePlacesById(placeId)
            }.start()
        }

        fun loadCollect(onLoaded: () -> Unit){
            Thread {
                PlaceData.collectPlaceList.addAll(FavoriteDataBase.getDataBase().getFavoriteDao().queryAllPlaces())
                onLoaded()
            }
        }

        fun saveAllCollect(onSaved: () -> Unit){
            Thread {
                FavoriteDataBase.getDataBase().getFavoriteDao().deleteAllPlaces()
                FavoriteDataBase.getDataBase().getFavoriteDao().insertAllPlaces(PlaceData.collectPlaceList)
                onSaved()
            }.start()
        }

        fun loadHistory(onLoaded: () -> Unit){
            Thread {
                PlaceData.searchHistoryList.addAll(HistoryDatabase.getDataBase().getPlaceDao().queryAllPlaces())
                onLoaded()
            }.start()
        }

        fun saveAllHistory(onSaved: () -> Unit){
            Thread {
                HistoryDatabase.getDataBase().getPlaceDao().deleteAllPlaces()
                HistoryDatabase.getDataBase().getPlaceDao().insertAllPlaces(PlaceData.collectPlaceList)
                onSaved()
            }.start()
        }

        fun insertHistory(place : Place){
            Thread{
                try {
                    HistoryDatabase.getDataBase().getPlaceDao().insertPlace(place)
                } catch ( e : SQLiteConstraintException){
                    e.printStackTrace()
                }
            }.start()
        }

        fun delHistory(placeId : Int){
            Thread {
                HistoryDatabase.getDataBase().getPlaceDao().deletePlacesById(placeId)
            }.start()
        }
    }

    private fun runThread(runnable: Runnable){
    }
}


