package com.example.KYL.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.KYL.dao.Dao
import com.example.KYL.entities.Coordinate


@Database(
    version = 2,
    entities = [Coordinate::class],
    autoMigrations = [AutoMigration(from = 1, to = 2)],
    exportSchema = true
)
abstract class MainDataBase : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: MainDataBase? = null

        fun getDataBase(contex: Context): MainDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    contex.applicationContext,
                    MainDataBase::class.java, "coordinate.db"
                ).build()
                instance
            }
        }
    }
}