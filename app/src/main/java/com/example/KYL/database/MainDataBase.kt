package com.example.KYL.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.KYL.dao.Dao
import com.example.KYL.entities.Coordinate

@Database(entities = [Coordinate::class], version = 2)
abstract class MainDataBase : RoomDatabase() {

    abstract fun getDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: MainDataBase? = null

        fun getDataBase(contex: Context): MainDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    contex.applicationContext,
                    MainDataBase::class.java, "coordinat.db"
                ).build()
                instance
            }
        }
    }
}