package com.mredrock.cyxbs.discover.map.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.Place

/**
 * @date 2020-08-15
 * @author Sca RayleighZ
 */
@Database(version = 1, entities = [Place::class])
abstract class HistoryDatabase : RoomDatabase(){
    abstract fun getPlaceDao () : PlaceDao
    companion object{
        private var instance : HistoryDatabase ?= null
        @Synchronized
        fun getDataBase(needToJoinIn : Boolean , whenGot : (HistoryDatabase) -> Unit  ){

            if (!needToJoinIn){
                instance?.let {
                    Thread{
                        whenGot(it)
                    }.start()
                    return
                }

                Thread{
                    Room.databaseBuilder(BaseApp.context.applicationContext,
                            HistoryDatabase :: class.java ,"app_history_database"
                    ).build().apply {
                        instance = this
                        whenGot(this)
                    }
                }.start()

                return
            }

            Thread{
                Room.databaseBuilder(BaseApp.context.applicationContext,
                        HistoryDatabase :: class.java ,"app_history_database"
                ).build().apply {
                    instance = this
                    whenGot(this)
                }
            }.start()
        }
    }
}