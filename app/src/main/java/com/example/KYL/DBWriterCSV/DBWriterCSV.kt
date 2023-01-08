package com.example.KYL.DBWriterCSV

import android.os.Environment
import android.util.Log
import java.io.File


class DBWriterCSV {


    val FILE_NAME = "koordDB.csv"
    val DIR_NAME = "MyFiles"


//    val csvFile = File(FILE_NAME)
//    val csvWrite = PrintWriter(csvFile)

    init {

    }

    fun writeFile() {
        val id: Int?
        val name: String
        val operationalnumberKIP: String?
        val operationalKM: String?
        val utsPipe: String?
        val uppPipe: String?
        val ipolPipe: String?
        val note: String?
        val time: String
        val utsСover: String?
        val uppCover: String?
        val ipolCover: String?
        val rPipeCover: String?
        val ups: String?
        val iprot: String?
        val depthPipe: String?
        val iPipe: String?
        val ues: String?
        val damageIP: String?
        val latitude: String
        val longitude: String
        val height: String
        val accuracy: String
        val speed: String

        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED
            )
        ) {
            Log.d("MyLog", "SD-карта не доступна: " + Environment.getExternalStorageState())
            return
        }
        // получаем путь к SD
        var sdPath = Environment.getExternalStorageDirectory()
        // добавляем свой каталог к пути
        sdPath = File(sdPath.getAbsolutePath() + "/" + DIR_NAME);
        // создаем каталог
        sdPath.mkdirs()
        // формируем объект File, который содержит путь к файлу
        val sdFile = File(sdPath, FILE_NAME)

        //val koordCursor =

            //Проходим циклом по строкам базы и записываем их в соотвтетствующие пеерменные


    }

}