package com.example.KYL.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.KYL.constans.MainDecimalFormat
import com.example.KYL.database.MainDataBase
import com.example.KYL.entities.Coordinate
import com.example.KYL.writerXLSX.WriteExcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook


class MainViewModel(dataBase: MainDataBase) : ViewModel() {
    val dao = dataBase.getDao()
    val allKoord: LiveData<List<Coordinate>> = dao.getAllKoordinate().asLiveData()
    private val _getAllCoordinate = MutableLiveData<List<Coordinate>>()
    val getAllCoordinate: LiveData<List<Coordinate>> get() = _getAllCoordinate


    init {
        getCoordinatList()
    }

    fun insertKoord(koordinate: Coordinate) = viewModelScope.launch {
        dao.insertKoordinate(koordinate)
    }

    fun insertKoordList(listCoord: List<Coordinate>) = viewModelScope.launch {
        dao.insertListKoordinate(listCoord)
    }

    fun updateKoord(koordinate: Coordinate) = viewModelScope.launch {
        dao.updateKoordinate(koordinate)
    }

    fun updateCoordForMove(koordinate: Coordinate, id: Int) = viewModelScope.launch {
        dao.updateKoordinate(koordinate)
        dao.updatePrimaryKeyForMove(id)
    }

    fun updateForMove(koordinate: Coordinate) = viewModelScope.launch {
        dao.updateKoordinate(koordinate)
    }

    fun updatePrimaryKeyForForMove(id: Int) = viewModelScope.launch {
        dao.updatePrimaryKeyForMove(id)
    }


    fun updateAllKoord(listCoord: List<Coordinate>) = viewModelScope.launch {
        dao.updateСhangeCoordinate(listCoord)
    }

    fun deleteKoord(id: Int) = viewModelScope.launch {
        dao.deleteKoordinate(id)
        dao.updatePrimaryKey(id)
    }

    fun deleteTable() = viewModelScope.launch {
        dao.deleteAllTable()
    }

//    fun getCoordinatList(): List<Coordinate> {
//        val getCoordinatList: Deferred<List<Coordinate>> =
//            viewModelScope.async {
//                val dataList = dao.getAllKoordinateList()
//                Log.d("Mytag", "getCoordinatList async: ${dataList.size}")
//                return@async dataList
//            }
//        return runBlocking {
//            val dataList = getCoordinatList.await()
//            Log.d("Mytag", "getCoordinatList runBlocking: ${dataList.size}")
//            return@runBlocking dataList
//        }
//    }

    fun getCoordinatList() {
        viewModelScope.launch {
            _getAllCoordinate.value = dao.getAllKoordinateList()
        }
    }


    fun getALLCoordinate() = dao.getAllKoordinate()

    @SuppressLint("SuspiciousIndentation")
    fun importDataBase(uri: Uri, context: Context) = viewModelScope.launch {
        val cellStringList: MutableList<String> = mutableListOf()
        val inputStream = context.contentResolver.openInputStream(uri)
        try {

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
                wbImport.close()
            }
        } catch (e: OLE2NotOfficeXmlFileException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Выбранный файл должен иметь раcширение .xlsx",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "$e", Toast.LENGTH_LONG).show()
            }
        } finally {
            inputStream?.close()
        }
        // Получаем список всех запятых
//        val commas = cellStringList.filter { it.contains(",") }
//
// Создаем новый список, заменяя запятые на точки
        val outputList = cellStringList.map {
            // Заменяем запятую на точку
            it.replace(",", ".")
        }
        val listCoordinate = createListCoordinate(outputList)
//        Log.d("MyTag", "importDataBase importList: ${listCoordinate}")

        listCoordinate.forEach {
            insertKoord(it)
        }
    }

    private fun createListCoordinate(cellStringList: List<String>): List<Coordinate> {
        val coordListBackup: ArrayList<Coordinate> = arrayListOf()
        val cellList =
            cellStringList.drop(32)
        val idList =
            cellList.slice(0..cellList.size - 1 step 32)
        val distanceList =
            cellList.slice(1 until cellList.size step 32)
        val nameList =
            cellList.slice(2 until cellList.size step 32)
        val operationalnumberKIPList =
            cellList.slice(3 until cellList.size step 32)
        val operationalKMList =
            cellList.slice(4 until cellList.size step 32)
        val utsPipeList =
            cellList.slice(5 until cellList.size step 32)
        val uppPipeList =
            cellList.slice(6 until cellList.size step 32)
        val ipolPipeList =
            cellList.slice(7 until cellList.size step 32)
        val uesddesList =
            cellList.slice(8 until cellList.size step 32)
        val znesddList =
            cellList.slice(9 until cellList.size step 32)
        val utsPipeList2 =
            cellList.slice(10 until cellList.size step 32)
        val uppPipeList2 =
            cellList.slice(11 until cellList.size step 32)
        val ipolPipeList2 =
            cellList.slice(12 until cellList.size step 32)
        val uesddesList2 =
            cellList.slice(13 until cellList.size step 32)
        val znesddList2 =
            cellList.slice(14 until cellList.size step 32)
        val noteList =
            cellList.slice(15 until cellList.size step 32)
        val timeList =
            cellList.slice(16 until cellList.size step 32)
        val utsСoverList =
            cellList.slice(17 until cellList.size step 32)
        val uppCoverList =
            cellList.slice(18 until cellList.size step 32)
        val ipolCoverList =
            cellList.slice(19 until cellList.size step 32)
        val rPipeCoverList =
            cellList.slice(20 until cellList.size step 32)
        val upsList =
            cellList.slice(21 until cellList.size step 32)
        val iprotList =
            cellList.slice(22 until cellList.size step 32)
        val depthPipeList =
            cellList.slice(23 until cellList.size step 32)
        val iPipeList =
            cellList.slice(24 until cellList.size step 32)
        val uesList =
            cellList.slice(25 until cellList.size step 32)
        val damageIPList =
            cellList.slice(26 until cellList.size step 32)
        val latitudeList =
            cellList.slice(27 until cellList.size step 32)
        val longitudeList =
            cellList.slice(28 until cellList.size step 32)
        val heightList =
            cellList.slice(29 until cellList.size step 32)
        val accuracyList =
            cellList.slice(30 until cellList.size step 32)
        val speedList =
            cellList.slice(31 until cellList.size step 32)

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
                        uesddes = uesddesList[i],
                        znesdd = znesddList[i],
                        utsPipe2 = utsPipeList2[i],
                        uppPipe2 = uppPipeList2[i],
                        ipolPipe2 = ipolPipeList2[i],
                        uesddes2 = uesddesList2[i],
                        znesdd2 = znesddList2[i],
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
                return emptyList()
            }
        }
        return coordListBackup
    }

    suspend fun createExcleTable(
        koordList: List<Coordinate>,
        fileName: String
    ) = withContext(Dispatchers.IO) {
        val wb: Workbook = XSSFWorkbook()
        var cell: Cell? = null
        var sheet: Sheet? = null

        sheet = wb.createSheet("Лист 1") // Создаем новый лист Excel
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
        cell.setCellValue("Uтз_1, В")

        cell = row.createCell(6)
        cell.setCellValue("Uпп_1, В")

        cell = row.createCell(7)
        cell.setCellValue("Ток поляризации ВЭ_!, мА")

        cell = row.createCell(8)
        cell.setCellValue("Uэсдд-эс_1, В")

        cell = row.createCell(9)
        cell.setCellValue("Зав. номер ЭСДД_1")

        cell = row.createCell(10)
        cell.setCellValue("Uтз_2, В")

        cell = row.createCell(11)
        cell.setCellValue("Uпп_2, В")

        cell = row.createCell(12)
        cell.setCellValue("Ток поляризации ВЭ_2, мА")

        cell = row.createCell(13)
        cell.setCellValue("Uэсдд-эс_2, В")

        cell = row.createCell(14)
        cell.setCellValue("Зав. номер ЭСДД_2")

        cell = row.createCell(15)
        cell.setCellValue("Примечание")

        cell = row.createCell(16)
        cell.setCellValue("Время")

        cell = row.createCell(17)
        cell.setCellValue("Uпатрон-земля, В")

        cell = row.createCell(18)
        cell.setCellValue("Uпп патрон, В")

        cell = row.createCell(19)
        cell.setCellValue(
            "Ток поляризации ВЭ патрон, мА"
        )

        cell = row.createCell(20)
        cell.setCellValue("Rтп, Ом")

        cell = row.createCell(21)
        cell.setCellValue("Uп-з, В")

        cell = row.createCell(22)
        cell.setCellValue("Iпр-с, мА")

        cell = row.createCell(23)
        cell.setCellValue("Глубина, м")

        cell = row.createCell(24)
        cell.setCellValue("Ток в трубе, мА")

        cell = row.createCell(25)
        cell.setCellValue("УЭС, Омхм")

        cell = row.createCell(26)
        cell.setCellValue("Повреждение ИП, м")

        cell = row.createCell(27)
        cell.setCellValue("Широта")

        cell = row.createCell(28)
        cell.setCellValue("Долгота")

        cell = row.createCell(29)
        cell.setCellValue("Высота, м")

        cell = row.createCell(30)
        cell.setCellValue("Точность, м")

        cell = row.createCell(31)
        cell.setCellValue("Скорость, м/с")

        for (i in 0..31) {
            sheet.setColumnWidth(i, (30 * 200))
        }
        //Проходим циклом, создаем и заполняем из коллекции таблицу Excel
        //       Log.d("MyTag", "koordList: $koordList")

        for (i in 0..(koordList.size - 1)) {
            val rowNext = sheet.createRow(i + 1)

            cell = rowNext.createCell(0)
            cell.setCellValue(MainDecimalFormat.formatExcelInt(i.toDouble()))

            cell = rowNext.createCell(1)
            cell.setCellValue(MainDecimalFormat.formatExcelInt(koordList[i].distance.toDouble()))

            cell = rowNext.createCell(2)
            cell.setCellValue(koordList[i].name)

            cell = rowNext.createCell(3)
            try {
                cell.setCellValue(koordList[i].operationalnumberKIP.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].operationalnumberKIP)
            }

            cell = rowNext.createCell(4)
            try {
                cell.setCellValue(koordList[i].operationalKM.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].operationalKM)
            }

            cell = rowNext.createCell(5)
            try {
                cell.setCellValue(koordList[i].utsPipe.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].utsPipe)
            }

            cell = rowNext.createCell(6)
            try {
                cell.setCellValue(koordList[i].uppPipe.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].uppPipe)
            }

            cell = rowNext.createCell(7)
            try {
                cell.setCellValue(koordList[i].ipolPipe.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].ipolPipe)
            }

            cell = rowNext.createCell(8)
            try {
                cell.setCellValue(koordList[i].uesddes.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].uesddes)
            }

            cell = rowNext.createCell(9)
            try {
                cell.setCellValue(koordList[i].znesdd.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].znesdd)
            }

            cell = rowNext.createCell(10)
            try {
                cell.setCellValue(koordList[i].utsPipe2.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].utsPipe2)
            }

            cell = rowNext.createCell(11)
            try {
                cell.setCellValue(koordList[i].uppPipe2.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].uppPipe2)
            }

            cell = rowNext.createCell(12)
            try {
                cell.setCellValue(koordList[i].ipolPipe2.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].ipolPipe2)
            }

            cell = rowNext.createCell(13)
            try {
                cell.setCellValue(koordList[i].uesddes2.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].uesddes2)
            }

            cell = rowNext.createCell(14)
            try {
                cell.setCellValue(koordList[i].znesdd2.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].znesdd2)
            }

            cell = rowNext.createCell(15)
            cell.setCellValue(koordList[i].note)

            cell = rowNext.createCell(16)
            cell.setCellValue(koordList[i].time)

            cell = rowNext.createCell(17)
            try {
                cell.setCellValue(koordList[i].utsСover.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].utsСover)
            }

            cell = rowNext.createCell(18)
            try {
                cell.setCellValue(koordList[i].uppCover.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].uppCover)
            }

            cell = rowNext.createCell(19)
            try {
                cell.setCellValue(koordList[i].ipolCover.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].ipolCover)
            }

            cell = rowNext.createCell(20)
            try {
                cell.setCellValue(koordList[i].rPipeCover.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].rPipeCover)
            }

            cell = rowNext.createCell(21)
            try {
                cell.setCellValue(koordList[i].ups.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].ups)
            }

            cell = rowNext.createCell(22)
            try {
                cell.setCellValue(koordList[i].iprot.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].iprot)
            }

            cell = rowNext.createCell(23)
            try {
                cell.setCellValue(koordList[i].depthPipe.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].depthPipe)
            }

            cell = rowNext.createCell(24)
            try {
                cell.setCellValue(koordList[i].iPipe.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].iPipe)
            }

            cell = rowNext.createCell(25)
            try {
                cell.setCellValue(koordList[i].ues.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].ues)
            }

            cell = rowNext.createCell(26)
            try {
                cell.setCellValue(koordList[i].damageIP.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].damageIP)
            }

            cell = rowNext.createCell(27)
            try {
                cell.setCellValue(koordList[i].latitude)
            } catch (e: NumberFormatException) {
                //               Log.d("Mytag", "createExcleTable: latitude = ${koordList[i].latitude}")
            }

            cell = rowNext.createCell(28)
            try {
                cell.setCellValue(koordList[i].longitude)
            } catch (e: NumberFormatException) {
//                Log.d("Mytag", "createExcleTable: latitude = ${koordList[i].longitude}")
            }

            cell = rowNext.createCell(29)
            try {
                cell.setCellValue(MainDecimalFormat.formatExcelTwoSings(koordList[i].height.toDouble()))
                //   cell.setCellValue(koordList[i].height.toDouble())
                //    cell.setCellValue(koordList[i].height)
//                Log.d("Mytag", "createExcleTable: height try = ${(MainDecimalFormat.formatExcelTwoSings(koordList[i].height.toDouble()))}")

            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].height)
                //              Log.d("Mytag", "createExcleTable: height catch = ${koordList[i].height}")
            }

            cell = rowNext.createCell(30)
            try {
                cell.setCellValue(koordList[i].accuracy.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].accuracy)
            }

            cell = rowNext.createCell(31)
            try {
                cell.setCellValue(koordList[i].speed.toDouble())
            } catch (e: NumberFormatException) {
                cell.setCellValue(koordList[i].speed)
            }


            for (i in 0..31) {
                sheet.setColumnWidth(i, (30 * 100))
                //sheet.autoSizeColumn(i)
            }
        }

        //Запись файла Excel в папку "Документы" телефона
        val writeExcel = WriteExcel(fileName)
        //fileName?.let { WriteExcel(fileName = it) } ?: WriteExcel(fileName = "Привет")
//        Log.d("MyTAG", "createExcleTable: $fileName")
        writeExcel.writeExcel(wb)
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