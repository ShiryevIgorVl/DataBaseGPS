package com.example.KYL.writerXLSX

import android.os.Environment
import com.example.KYL.constans.MainTime
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class WriteExcel(APP_NAME: String) {

    val FILE_NAME = APP_NAME + " " + MainTime.getTimeForSaveFile() + ".xlsx"

   fun writeExcel(wb: Workbook) {
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

        val file = File(path, FILE_NAME)

        val streamWrite: FileOutputStream
        try {
            if (!path.exists()) {
                path.mkdirs()
            }
            streamWrite = FileOutputStream(file)
            wb.write(streamWrite)
            streamWrite.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}
