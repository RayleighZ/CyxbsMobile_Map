package com.mredrock.cyxbs.discover.map.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mredrock.cyxbs.discover.map.bean.Place

/**
 * @date 2020-08-08
 * @author Sca RayleighZ
 * 获取database请异步执行
 */
@Database(version = 1 , entities = [Place :: class])
abstract class PlaceDatabase : RoomDatabase(){
    abstract fun getPlaceDao () : PlaceDao
    companion object{
        private var instance : PlaceDatabase ?= null
        @Synchronized
        fun getDataBase(context: Context) : PlaceDatabase{
            instance?.let {
                return it
            }

            return Room.databaseBuilder(context.applicationContext,
                    PlaceDatabase :: class.java ,"app_database"
            ).build().apply {
                instance = this
            }
        }
    }
}