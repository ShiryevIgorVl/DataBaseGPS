package com.example.KYL.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import com.example.KYL.database.MainDataBase
import com.example.KYL.entities.Coordinate
import com.example.KYL.writerXLSX.WriteExcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook


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
//        Log.d("MyTag", "importDataBase importList: ${listCoordinate}")

        listCoordinate.forEach {
            insertKoord(it)
        }
    }

    private fun createListCoordinate(cellStringList: List<String>): List<Coordinate> {
        val coordListBackup: ArrayList<Coordinate> = arrayListOf()
        val cellList =
            cellStringList.drop(25)
        val idList =
            cellList.slice(0..cellList.size - 1 step 25)
        val distanceList =
            cellList.slice(1 until cellList.size step 25)
        val nameList =
            cellList.slice(2 until cellList.size step 25)
        val operationalnumberKIPList =
            cellList.slice(3 until cellList.size step 25)
        val operationalKMList =
            cellList.slice(4 until cellList.size step 25)
        val utsPipeList =
            cellList.slice(5 until cellList.size step 25)
        val uppPipeList =
            cellList.slice(6 until cellList.size step 25)
        val ipolPipeList =
            cellList.slice(7 until cellList.size step 25)
        val noteList =
            cellList.slice(8 until cellList.size step 25)
        val timeList =
            cellList.slice(9 until cellList.size step 25)
        val utsСoverList =
            cellList.slice(10 until cellList.size step 25)
        val uppCoverList =
            cellList.slice(11 until cellList.size step 25)
        val ipolCoverList =
            cellList.slice(12 until cellList.size step 25)
        val rPipeCoverList =
            cellList.slice(13 until cellList.size step 25)
        val upsList =
            cellList.slice(14 until cellList.size step 25)
        val iprotList =
            cellList.slice(15 until cellList.size step 25)
        val depthPipeList =
            cellList.slice(16 until cellList.size step 25)
        val iPipeList =
            cellList.slice(17 until cellList.size step 25)
        val uesList =
            cellList.slice(18 until cellList.size step 25)
        val damageIPList =
            cellList.slice(19 until cellList.size step 25)
        val latitudeList =
            cellList.slice(20 until cellList.size step 25)
        val longitudeList =
            cellList.slice(21 until cellList.size step 25)
        val heightList =
            cellList.slice(22 until cellList.size step 25)
        val accuracyList =
            cellList.slice(23 until cellList.size step 25)
        val speedList =
            cellList.slice(24 until cellList.size step 25)

//        Log.d("MyTag", "createListPoint speedList: ${speedList}")
//        Log.d(TAG, "createListPoint nameList: ${nameList}")
//        Log.d(TAG, "createListPoint ageList: ${ageList}")
//        Log.d(TAG, "createListPoint размер: ${cellList.size}")
        for (i in idList.indices) {
//                  Log.d("MyTag", "idListToInt: ${idList}")
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

//                Log.d("MyTag", "createListCoordinate Coordinate Exception : ${e}")
            }

//            Log.d("MyTag", "createListCoordinate Coordinate Exception : ${coordListBackup}")
        }
//        Log.d(TAG, "createListPoint Point после for: ${pointListBackup}")
        return coordListBackup
    }

   suspend fun createExcleTable(
        koordList: List<Coordinate>,
        APP_NAME: String
    ) = withContext(Dispatchers.IO) {
        val wb: Workbook = XSSFWorkbook()
        var cell: Cell? = null
        var sheet: Sheet? = null

        sheet = wb.createSheet("$APP_NAME") // Создаем новый лист Excel
        val row: Row = sheet.createRow(0) // Создаем новую строку

        cell = row.createCell(0) //В этой строке создаем новую ячейку
        cell.setCellValue("№ п.п.") //В ячейку пишем значение

        cell = row.createCell(1)
        cell.setCellValue("Дистанция, м")

        cell = row.createCell(2)
        cell.setCellValue("Наименование")

        cell = row.createCell(3)
        cell.setCellValue("Номер КИП")

        cell = row.createCell(4)
        cell.setCellValue("КИП км")

        cell = row.createCell(5)
        cell.setCellValue("Uтз, В")

        cell = row.createCell(6)
        cell.setCellValue("Uпп, В")

        cell = row.createCell(7)
        cell.setCellValue("Ток поляризации ВЭ, мА")

        cell = row.createCell(8)
        cell.setCellValue("Примечание")

        cell = row.createCell(9)
        cell.setCellValue("Время")

        cell = row.createCell(10)
        cell.setCellValue("Uпатрон-земля, В")

        cell = row.createCell(11)
        cell.setCellValue("Uпп патрон, В")

        cell = row.createCell(12)
        cell.setCellValue(
            "Ток поляризации ВЭ патрон, мА"
        )

        cell = row.createCell(13)
        cell.setCellValue("Rтп, Ом")

        cell = row.createCell(14)
        cell.setCellValue("Uп-з, В")

        cell = row.createCell(15)
        cell.setCellValue("Iпр-с, мА")

        cell = row.createCell(16)
        cell.setCellValue("Глубина, м")

        cell = row.createCell(17)
        cell.setCellValue("Ток в трубе, мА")

        cell = row.createCell(18)
        cell.setCellValue("УЭС, Омхм")

        cell = row.createCell(19)
        cell.setCellValue("Повреждение ИП, м")

        cell = row.createCell(20)
        cell.setCellValue("Широта")

        cell = row.createCell(21)
        cell.setCellValue("Долгота")

        cell = row.createCell(22)
        cell.setCellValue("Высота, м")

        cell = row.createCell(23)
        cell.setCellValue("Точность, м")

        cell = row.createCell(24)
        cell.setCellValue("Скорость, м/с")

        for (i in 0..24) {
            sheet.setColumnWidth(i, (30 * 200))
        }
        //Проходим циклом, создаем и заполняем из коллекции таблицу Excel


        for (i in 0..(koordList.size - 1)) {
            val rowNext = sheet.createRow(i + 1)

            cell = rowNext.createCell(0)
            cell.setCellValue("$i")

            cell = rowNext.createCell(1)
            cell.setCellValue(koordList[i].distance.toString())

            cell = rowNext.createCell(2)
            cell.setCellValue(koordList[i].name)

            cell = rowNext.createCell(3)
            cell.setCellValue(koordList[i].operationalnumberKIP)

            cell = rowNext.createCell(4)
            cell.setCellValue(koordList[i].operationalKM)

            cell = rowNext.createCell(5)
            cell.setCellValue(koordList[i].utsPipe)

            cell = rowNext.createCell(6)
            cell.setCellValue(koordList[i].uppPipe)

            cell = rowNext.createCell(7)
            cell.setCellValue(koordList[i].ipolPipe)

            cell = rowNext.createCell(8)
            cell.setCellValue(koordList[i].note)

            cell = rowNext.createCell(9)
            cell.setCellValue(koordList[i].time)

            cell = rowNext.createCell(10)
            cell.setCellValue(koordList[i].utsСover)

            cell = rowNext.createCell(11)
            cell.setCellValue(koordList[i].uppCover)

            cell = rowNext.createCell(12)
            cell.setCellValue(koordList[i].ipolCover)

            cell = rowNext.createCell(13)
            cell.setCellValue(koordList[i].rPipeCover)

            cell = rowNext.createCell(14)
            cell.setCellValue(koordList[i].ups)

            cell = rowNext.createCell(15)
            cell.setCellValue(koordList[i].iprot)

            cell = rowNext.createCell(16)
            cell.setCellValue(koordList[i].depthPipe)

            cell = rowNext.createCell(17)
            cell.setCellValue(koordList[i].iPipe)

            cell = rowNext.createCell(18)
            cell.setCellValue(koordList[i].ues)

            cell = rowNext.createCell(19)
            cell.setCellValue(koordList[i].damageIP)

            cell = rowNext.createCell(20)
            cell.setCellValue(koordList[i].latitude)

            cell = rowNext.createCell(21)
            cell.setCellValue(koordList[i].longitude)

            cell = rowNext.createCell(22)
            cell.setCellValue(koordList[i].height)

            cell = rowNext.createCell(23)
            cell.setCellValue(koordList[i].accuracy)

            cell = rowNext.createCell(24)
            cell.setCellValue(koordList[i].speed)

            for (i in 0..24) {
                sheet.setColumnWidth(i, (30 * 100))
                //sheet.autoSizeColumn(i)
            }
        }

        //Запись файла Excel в папку "Документы" телефона
        val writeExcel = APP_NAME?.let { WriteExcel(APP_NAME = it) }
       viewModelScope.launch {  writeExcel?.writeExcel(wb) }
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