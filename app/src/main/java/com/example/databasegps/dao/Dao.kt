package com.example.databasegps.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.databasegps.entities.Coordinate
import kotlinx.coroutines.flow.Flow



@Dao
interface Dao {
   //Запись в DB
    @Insert
    suspend fun insertKoordinate(koordinate: Coordinate)

    //Считывание все из DB (запрос) автоматически и постоянно при изменении в DB возвращает поток списков координат (Entities)
    // и запускаем не из корутин (не suspend функция)потому,что есть Flow
    @Query ("SELECT * FROM koordinate")
    fun getAllKoordinate (): Flow<List<Coordinate>>

    //Удаление из БД
    @Query ("DELETE FROM koordinate WHERE id IS :id")
    suspend fun deleteKoordinate(id: Int)

    // Получение всех записей
    @Query ("SELECT * FROM koordinate")
    fun getAllKoordinateList (): List<Coordinate>
}