package com.mredrock.cyxbs.discover.map.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mredrock.cyxbs.common.BaseApp
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
        fun getDataBase(needToJoinIn : Boolean , whenGot : (PlaceDatabase) -> Unit){

            if (!needToJoinIn){
                instance?.let {
                    Thread{
                        whenGot(it)
                    }.start()
                    return
                }

                Thread{
                    Room.databaseBuilder(BaseApp.context.applicationContext,
                            PlaceDatabase :: class.java ,"app_placedata_database"
                    ).build().apply {
                        instance = this
                        whenGot(this)
                    }
                }.start()
                return
            }

            Thread{
                Room.databaseBuilder(BaseApp.context.applicationContext,
                        PlaceDatabase :: class.java ,"app_placedata_database"
                ).build().apply {
                    instance = this
                    whenGot(this)
                }
            }.start()
            /*return Room.databaseBuilder(BaseApp.context.applicationContext,
                    PlaceDatabase :: class.java ,"app_placedata_database"
            ).build().apply {
                instance = this
            }*/
        }
    }
}