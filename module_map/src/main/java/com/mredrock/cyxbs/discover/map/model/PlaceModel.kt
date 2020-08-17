package com.mredrock.cyxbs.discover.map.model

import com.mredrock.cyxbs.common.utils.LogUtils
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

        fun loadAllData( needToJoinIn: Boolean,onLoaded: () -> Unit) {
            loadPlace(needToJoinIn) { }
            loadCollect(needToJoinIn) { }
            loadHistory(needToJoinIn) { }
            onLoaded()
        }

        fun loadPlace(needToJoinIn : Boolean,onLoaded: () -> Unit) {
            PlaceDatabase.getDataBase(needToJoinIn) {
                PlaceData.placeList.clear()
                PlaceData.placeList.addAll(it.getPlaceDao().queryAllPlaces())
            }
            onLoaded()
        }

        //全部存储，由于索引为ID，所以如果不清除直接添加会报错
        fun saveAllPlace(needToJoinIn : Boolean,onSaved: () -> Unit) {
            PlaceDatabase.getDataBase(needToJoinIn) {
                it.getPlaceDao().deleteAllPlaces()
                it.getPlaceDao().insertAllPlaces(PlaceData.placeList)
            }
            onSaved()
        }

        fun insertCollectPlace(needToJoinIn : Boolean,place: Place) {
            FavoriteDataBase.getDataBase(needToJoinIn) {
                it.getFavoriteDao().insertPlace(place)
            }
        }

        fun delCollectPlace(needToJoinIn : Boolean,placeId: Int) {
            FavoriteDataBase.getDataBase(needToJoinIn) {
                it.getFavoriteDao().deletePlacesById(placeId)
            }
        }

        fun loadCollect(needToJoinIn : Boolean,onLoaded: () -> Unit) {
            FavoriteDataBase.getDataBase(needToJoinIn) {
                PlaceData.collectPlaceList.clear()
                PlaceData.collectPlaceList.addAll(it.getFavoriteDao().queryAllPlaces())
                onLoaded()
            }
        }


        fun saveAllCollect(needToJoinIn : Boolean,onSaved: () -> Unit) {
            FavoriteDataBase.getDataBase(needToJoinIn){
                LogUtils.e("---","${PlaceData.collectPlaceList.size}")
                it.getFavoriteDao()
                        .deleteAllPlaces()
                it.getFavoriteDao().insertAllPlaces(PlaceData.collectPlaceList)
            }
            onSaved()
        }

        fun loadHistory(needToJoinIn : Boolean,onLoaded: () -> Unit) {
            HistoryDatabase.getDataBase(needToJoinIn) {
                PlaceData.searchHistoryList.clear()
                PlaceData.searchHistoryList.addAll(it.getPlaceDao().queryAllPlaces())
            }
            onLoaded()
        }

        fun saveAllHistory(needToJoinIn : Boolean,onSaved: () -> Unit) {
            HistoryDatabase.getDataBase(needToJoinIn) {
                it.getPlaceDao().deleteAllPlaces()
                it.getPlaceDao().insertAllPlaces(PlaceData.searchHistoryList)
            }
            onSaved()
        }

        fun insertHistory(needToJoin : Boolean,place: Place) {
            HistoryDatabase.getDataBase(needToJoin) {
                it.getPlaceDao().insertPlace(place)
            }
        }

        fun delHistory(needToJoinIn : Boolean,placeId: Int) {
            HistoryDatabase.getDataBase(needToJoinIn) {
                it.getPlaceDao().deletePlacesById(placeId)
            }
        }
    }
}

