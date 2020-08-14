package com.mredrock.cyxbs.discover.map.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mredrock.cyxbs.discover.map.bean.FavoritePlace
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData

/**
 * @date 2020-08-14
 * @author Sca RayleighZ
 */
@Dao
interface CollectPlaceDao {
    @Insert
    fun insertAllPlaces(favoriteList : MutableList<FavoritePlace>)//一次性添加所有的地址

    @Insert
    fun insertPlace(favoritePlace: FavoritePlace)//添加单个地址

    @Query("SELECT * FROM favorites")
    fun queryAllFavorites(): List<FavoritePlace>//获取全部地址

    @Query("DELETE FROM favorites")
    fun deleteAllFavorites()//删除全部地址

    @Query("DELETE FROM favorites WHERE placeId = :id")
    fun deleteFavoritesById(id: Int)//根据ID删除地点

    @Update
    fun updateFavoritePlace(favoritePlace: FavoritePlace)//更新地点数据
}