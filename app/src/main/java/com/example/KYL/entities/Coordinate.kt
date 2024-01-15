package com.example.KYL.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity (tableName = "coordinate")
data class Coordinate(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int?,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "distance") var distance: Int = 0,
    @ColumnInfo(name = "operational_number_KIP") val operationalnumberKIP: String = "",
    @ColumnInfo(name = "operational_km") val operationalKM: String = "",
    @ColumnInfo(name = "uts_pipe") val utsPipe: String = "",
    @ColumnInfo(name = "upp_pipe") val uppPipe: String = "",
    @ColumnInfo(name = "ipol_ve_pipe") val ipolPipe: String = "",
    @ColumnInfo(name = "note") val note: String = "",
    @ColumnInfo(name = "time") val time: String = "",
    @ColumnInfo(name = "uts_cover") val utsСover: String = "",
    @ColumnInfo(name = "upp_cover") val uppCover: String = "",
    @ColumnInfo(name = "ipol_ve_cover") val ipolCover: String = "",
    @ColumnInfo(name = "res_pipe_cover") val rPipeCover: String = "",
    @ColumnInfo(name = "u_prot_semlya") val ups: String = "",
    @ColumnInfo(name = "i_prot") val iprot: String = "",
    @ColumnInfo(name = "depth_pipe") val depthPipe: String = "",
    @ColumnInfo(name = "i_in_pipe") val iPipe: String = "",
    @ColumnInfo(name = "ues") val ues: String = "",
    @ColumnInfo(name = "damage_IP") val damageIP: String = "",
    @ColumnInfo(name = "latitude") val latitude: Double = 0.0,
    @ColumnInfo(name = "longitude") val longitude: Double = 0.0,
    @ColumnInfo(name = "height") val height: String = "",
    @ColumnInfo(name = "accuracy") val accuracy: String = "",
    @ColumnInfo(name = "speed") val speed: String = ""
) : Serializable

