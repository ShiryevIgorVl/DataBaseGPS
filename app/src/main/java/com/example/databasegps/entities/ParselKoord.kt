package com.example.databasegps.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.io.Serializable

open class ParselKoord(
   val latitude: String?,
   val longitude: String?,
   val height: String?,
   val accuracy: String?,
   val speed: String?
  ): Serializable