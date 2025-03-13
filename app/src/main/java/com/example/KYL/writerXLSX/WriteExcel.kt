package com.example.KYL.writerXLSX
import android.os.Environment
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.io.FileOutputStream

class WriteExcel(fileName: String) {

    val FILE_NAME = "$fileName.xlsx"

    fun writeExcel(wb: Workbook): Boolean {
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

        val file = File(path, FILE_NAME)

        val streamWrite: FileOutputStream
        try {
            if (!path.exists()) {
                path.mkdirs()
            }
            if (file.exists()){
                file.delete()
            }
            streamWrite = FileOutputStream(file)
            wb.write(streamWrite)
            streamWrite.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
            }
        }
    }

