package com.example.KYL.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.KYL.entities.Coordinate
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    //Запись в DB
    @Insert
    suspend fun insertKoordinate(koordinate: Coordinate)

    //Считывание все из DB (запрос) автоматически и постоянно при изменении в DB возвращает поток списков координат (Entities)
    // и запускаем не из корутин (не suspend функция)потому,что есть Flow
    @Query("SELECT * FROM coordinat")
    fun getAllKoordinate(): Flow<List<Coordinate>>

    //Удаление из БД
    @Query("DELETE FROM coordinat WHERE id IS :id")
    suspend fun deleteKoordinate(id: Int)

    // Получение всех записей
    @Query("SELECT * FROM coordinat")
    fun getAllKoordinateList(): List<Coordinate>

    //Считывание последней записи из DB
    @Query("SELECT * FROM coordinat WHERE distance = (SELECT MAX(distance) FROM coordinat)")
    fun getLastCoordinate(): Coordinate?
}