package com.example.KYL.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.KYL.database.MainDataBase

import com.example.KYL.entities.Coordinate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook


//@Suppress("UNREACHABLE_CODE", "UNCHECKED_CAST")
class MainViewModel(dataBase: MainDataBase) : ViewModel() {
    val dao = dataBase.getDao()
    val allKoord: LiveData<List<Coordinate>> = dao.getAllKoordinate().asLiveData()

    fun insertKoord(koordinate: Coordinate) = viewModelScope.launch {
        dao.insertKoordinate(koordinate)
    }

    fun insertKoordList(listCoord: List<Coordinate>) = viewModelScope.launch {
        dao.insertListKoordinate(listCoord)
    }

    fun updateKoord(koordinate: Coordinate) = viewModelScope.launch {
        dao.updateKoordinate(koordinate)
    }

    fun updateAllKoord(listCoord: List<Coordinate>) = viewModelScope.launch {
        dao.updateСhangeCoordinate(listCoord)
    }

    fun deleteKoord(id: Int) = viewModelScope.launch {
        dao.deleteKoordinate(id)
    }

    fun deleteTable() = viewModelScope.launch {
        dao.deleteAllTable()
    }

    fun getCoordinatList() = dao.getAllKoordinateList()

    fun getLastCoordinate() = dao.getLastCoordinate()

    @SuppressLint("SuspiciousIndentation")
    fun importDataBase(uri: Uri, context: Context) = viewModelScope.launch {
        val cellStringList: MutableList<String> = mutableListOf()
        val inputStream = context.contentResolver.openInputStream(uri)
        val wbImport = XSSFWorkbook(inputStream)
        val sheet = wbImport.getSheetAt(0)
//        cellStringList.clear()
//        val firstRow: Row = sheet.getRow(0)      //первая строка в первом листе
//        val lastNumRow = sheet.lastRowNum.toInt()       //номер  последней строки в первом листе
//        val firstCeel: Cell = firstRow.getCell(0)    //первая ячейка в первой строке
//        val lastNumCeel = firstRow.lastCellNum.toInt()   //номер  последней ячейки в первой строке

//        Log.d(TAG, "importDataBase lastNumRow: $lastNumRow")
//        Log.d(TAG, "importDataBase lastNumCeel: $lastNumCeel")

        for (row: Row in sheet) {
            for (cell: Cell in row) {
                if (cell.cellType == CellType.BLANK) {
                    cellStringList.add("пусто")
                } else {
                    when (cell.cellType) {
                        CellType.NUMERIC -> cellStringList.add(cell.numericCellValue.toString())
                        CellType.STRING -> cellStringList.add(cell.stringCellValue)
                        else -> break
                    }
                }
            }
        }

        wbImport.close()

        val listCoordinate = createListCoordinate(cellStringList)
        Log.d("MyTag", "importDataBase importList: ${listCoordinate}")
        updateAllKoord(listCoordinate)
    }

    private fun createListCoordinate(cellStringList: List<String>): List<Coordinate> {
        val coordListBackup: ArrayList<Coordinate> = arrayListOf()
        val cellList =
            cellStringList.drop(25)
        val idList =
            cellList.slice(0..cellList.size - 1 step 25)
        val distanceList =
            cellList.slice(1..cellList.size - 1 step 25)
        val nameList =
            cellList.slice(2..cellList.size - 1 step 25)
        val operationalnumberKIPList =
            cellList.slice(3..cellList.size - 1 step 25)
        val operationalKMList =
            cellList.slice(4..cellList.size - 1 step 25)
        val utsPipeList =
            cellList.slice(5..cellList.size - 1 step 25)
        val uppPipeList =
            cellList.slice(6..cellList.size - 1 step 25)
        val ipolPipeList =
            cellList.slice(7..cellList.size - 1 step 25)
        val noteList =
            cellList.slice(8..cellList.size - 1 step 25)
        val timeList =
            cellList.slice(9..cellList.size - 1 step 25)
        val utsСoverList =
            cellList.slice(10..cellList.size - 1 step 25)
        val uppCoverList =
            cellList.slice(11..cellList.size - 1 step 25)
        val ipolCoverList =
            cellList.slice(12..cellList.size - 1 step 25)
        val rPipeCoverList =
            cellList.slice(13..cellList.size - 1 step 25)
        val upsList =
            cellList.slice(14..cellList.size - 1 step 25)
        val iprotList =
            cellList.slice(15..cellList.size - 1 step 25)
        val depthPipeList =
            cellList.slice(16..cellList.size - 1 step 25)
        val iPipeList =
            cellList.slice(17..cellList.size - 1 step 25)
        val uesList =
            cellList.slice(18..cellList.size - 1 step 25)
        val damageIPList =
            cellList.slice(19..cellList.size - 1 step 25)
        val latitudeList =
            cellList.slice(20..cellList.size - 1 step 25)
        val longitudeList =
            cellList.slice(21..cellList.size - 1 step 25)
        val heightList =
            cellList.slice(22..cellList.size - 1 step 25)
        val accuracyList =
            cellList.slice(23..cellList.size - 1 step 25)
        val speedList =
            cellList.slice(24..cellList.size - 1 step 25)

//        Log.d("MyTag", "createListPoint speedList: ${speedList}")
//        Log.d(TAG, "createListPoint nameList: ${nameList}")
//        Log.d(TAG, "createListPoint ageList: ${ageList}")
//        Log.d(TAG, "createListPoint размер: ${cellList.size}")
        for (i in 0..idList.size-1) {
                  Log.d("MyTag", "idListToInt: ${idList}")
            try {
                coordListBackup.add(
                    Coordinate(
                        id = idList[i].toDouble().toInt(),
                        name = nameList[i],
                        distance = distanceList[i].toDouble().toInt(),
                        operationalnumberKIP = operationalnumberKIPList[i],
                        operationalKM = operationalKMList[i],
                        utsPipe = utsPipeList[i],
                        uppPipe = uppPipeList[i],
                        ipolPipe = ipolPipeList[i],
                        note = noteList[i],
                        time = timeList[i],
                        utsСover = utsСoverList[i],
                        uppCover = uppCoverList[i],
                        ipolCover = ipolCoverList[i],
                        rPipeCover = rPipeCoverList[i],
                        ups = upsList[i],
                        iprot = iprotList[i],
                        depthPipe = depthPipeList[i],
                        iPipe = iPipeList[i],
                        ues = uesList[i],
                        damageIP = damageIPList[i],
                        latitude = latitudeList[i].toDouble(),
                        longitude = longitudeList[i].toDouble(),
                        height = heightList[i],
                        accuracy = accuracyList[i],
                        speed = speedList[i]
                    )
                )

            } catch (e: Exception) {
//              Toast.makeText(withContext(), "Ошибка записи в Point ${e}", Toast.LENGTH_LONG).show()
//                coordListBackup.removeAt(i)
//                coordListBackup.add(
//                    i,
//                    Coordinate(
//                        id = i + 1,
//                        name = "",
//                        distance = 0,
//                        operationalnumberKIP = "",
//                        operationalKM = "",
//                        utsPipe = "",
//                        uppPipe = "",
//                        ipolPipe = "",
//                        note = "",
//                        time = "",
//                        utsСover = "",
//                        uppCover = "",
//                        rPipeCover = "",
//                        ups = "",
//                        iprot = "",
//                        depthPipe = "",
//                        iPipe = "",
//                        ues = "",
//                        damageIP = "",
//                        latitude = 0.0,
//                        longitude = 0.0,
//                        height = "",
//                        accuracy = "",
//                        speed = ""
//                    )
//                )

                Log.d("MyTag", "createListCoordinate Coordinate Exception : ${e}")
            }

            Log.d("MyTag", "createListCoordinate Coordinate Exception : ${coordListBackup}")
        }
//        Log.d(TAG, "createListPoint Point после for: ${pointListBackup}")
        return coordListBackup
    }


    @Suppress("UNCHECKED_CAST")
    //В соответствии с рекомендациями Google Android
    class MainViewModelFactory(val dataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

                return MainViewModel(dataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}