package com.example.databasegps.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.databasegps.entities.Koordinate
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
   //Запись в DB
    @Insert
    suspend fun insertKoordinate(koordinate: Koordinate)

    //Считывание все из DB (запрос) автоматически и постоянно при изменении в DB возвращает поток списков координат (Entities)
    // и запускаем не из корутин (не suspend функция)потому,что есть Flow
    @Query ("SELECT * FROM koordinate")
    fun getAllKoordinate (): Flow<List<Koordinate>>

    //Удаление из БД
    @Query ("DELETE FROM koordinate WHERE id IS :id")
    suspend fun deleteKoordinate(id: Int)
}