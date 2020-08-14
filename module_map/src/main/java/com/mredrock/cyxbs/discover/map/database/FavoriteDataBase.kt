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

@Database(version = 2, entities = [FavoritePlace::class])
abstract class FavoriteDataBase : RoomDatabase(){
    abstract fun getFavoriteDao(): CollectPlaceDao

    companion object {
        private var instance: FavoriteDataBase? = null

        @Synchronized
        fun getDataBase(context: Context): FavoriteDataBase {
            instance?.let {
                return it
            }

            return Room.databaseBuilder(context.applicationContext,
                    FavoriteDataBase :: class.java ,"app_favorite_database"
            ).build().apply {
                FavoriteDataBase.instance = this
            }
        }
    }
}
