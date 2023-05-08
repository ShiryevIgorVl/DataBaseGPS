package com.example.KYL.dao

import androidx.room.*
import androidx.room.Dao
import com.example.KYL.entities.Coordinate
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    //Запись в DB
    @Insert
    suspend fun insertKoordinate(koordinate: Coordinate)

    @Insert
    suspend fun insertListKoordinate(listCoord: List<Coordinate>)

    //Обновление элементов DB
    @Update
    suspend fun updateKoordinate (koordinate: Coordinate)

    //Считывание все из DB (запрос) автоматически и постоянно при изменении в DB возвращает поток списков координат (Entities)
    // и запускаем не из корутин (не suspend функция)потому,что есть Flow
    @Query("SELECT * FROM coord")
    fun getAllKoordinate(): Flow<List<Coordinate>>

    //Удаление из БД
    @Query("DELETE FROM coord WHERE id IS :id")
    suspend fun deleteKoordinate(id: Int)

    //Удаление всей DB
    @Query("DELETE FROM coord")
    suspend fun deleteAllTable()

    // Получение всех записей
    @Query("SELECT * FROM coord")
    fun getAllKoordinateList(): List<Coordinate>

    //Считывание последней записи из DB
    @Query("SELECT * FROM coord WHERE distance = (SELECT MAX(distance) FROM coord)")
    fun getLastCoordinate(): Coordinate?

    //Обнавление DB при замене местами двух записей
    @Update
    suspend fun updateСhangeCoordinate(listCoord: List<Coordinate>)

    //Полечени двух записей Coordinate для обмена местами в DB
    @Query("SELECT * FROM coord WHERE id IN (:id1, :id2)")
    suspend fun getCoordinateFromChange(id1: Int, id2: Int): List<Coordinate>
}