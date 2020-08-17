package com.mredrock.cyxbs.discover.map.database

import androidx.room.*
import com.mredrock.cyxbs.discover.map.bean.Place

/**
 * @date 2020-08-08
 * @author Sca RayleighZ
 */

@Dao
interface PlaceDao {

    @Insert
    fun insertAllPlaces(places : MutableList<Place>)//一次性添加所有的地址

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(place : Place)//添加单个地址

    @Query( "SELECT * FROM places")
    fun queryAllPlaces(): List<Place>//获取全部地址

    @Query("DELETE FROM places")
    fun deleteAllPlaces()//删除全部地址

    @Query("DELETE FROM places WHERE placeId = :id")
    fun deletePlacesById(id: Int)//根据ID删除地点

    @Update
    fun updatePlace(place : Place)//更新地点数据
}