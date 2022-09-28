package com.example.databasegps.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Koordinate(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "latitude") val latitude: String,
    @ColumnInfo(name = "longitude") val longitude: String,
    @ColumnInfo(name = "height") val height: String,
    @ColumnInfo(name = "accuracy") val accuracy: String,
    @ColumnInfo(name = "speed") val speed: String,
    @ColumnInfo(name = "note") val note: String

) : Serializable

