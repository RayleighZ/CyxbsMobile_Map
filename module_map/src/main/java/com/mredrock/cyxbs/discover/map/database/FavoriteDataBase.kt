package com.mredrock.cyxbs.discover.map.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.FavoritePlace
import com.mredrock.cyxbs.discover.map.bean.Place

/**
 * @date 2020-08-15
 * @author Sca RayleighZ
 */

@Database(version = 2, entities = [Place::class])
abstract class FavoriteDataBase : RoomDatabase(){
    abstract fun getFavoriteDao(): PlaceDao

    companion object {
        private var instance: FavoriteDataBase? = null

        @Synchronized
        fun getDataBase(): FavoriteDataBase {
            instance?.let {
                return it
            }

            return Room.databaseBuilder(BaseApp.context.applicationContext,
                    FavoriteDataBase :: class.java ,"app_favorite_database"
            ).build().apply {
                instance = this
            }
        }
    }
}
