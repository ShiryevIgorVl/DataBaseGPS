package com.example.KYL.fragments

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.KYL.R
import com.example.KYL.activity.App
import com.example.KYL.activity.CoordActivity
import com.example.KYL.constans.MainTime
import com.example.KYL.databinding.FragmentCoordBinding
import com.example.KYL.entities.Coordinate
import com.example.KYL.recyclerview.CoordAdapter
import com.example.KYL.recyclerview.ItemTouchHelperAdapter
import com.example.KYL.recyclerview.ItemTouchHelperCallback
import com.example.KYL.service.LocationService
import com.example.KYL.viewmodel.MainViewModel
import com.example.KYL.writerXLS.WriteExcel
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import java.util.Collections


class CoordFragment : BaseFragment(), CoordAdapter.Listener, ItemTouchHelperAdapter {

    private lateinit var binding: FragmentCoordBinding
    private lateinit var coordResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: CoordAdapter
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }

    override fun onClickNew() {
        coordResultLauncher.launch(Intent(activity, CoordActivity::class.java))
    }

    private fun onCoordResult() {
        coordResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val editState = it.data?.getStringExtra(STATE_KOORD)
                    val coordinate = it.data?.getSerializableExtra(KOORD_KEY) as Coordinate
                    val koordList = adapter.getData()
                    Log.d("MyLog", "onCoordResult koordList: $koordList")

                    if (editState == "update") {
                        mainViewModel.updateKoord(coordinate)
                    } else {
                        if (koordList.size != 0) {
                            var distance = koordList[koordList.size - 1].distance
                            val _distance = getDistance(
                                koordList[koordList.size - 1].latitude,
                                koordList[koordList.size - 1].longitude,
                                coordinate.latitude,
                                coordinate.longitude
                            )
                            distance += _distance
                            coordinate.distance = distance
                            coordinate.id = koordList.size
                            Log.d("MyLog", "onCoordResult distance: $distance")
                            Log.d("MyLog", "onCoordResult coordinate: $coordinate")
                            mainViewModel.insertKoord(coordinate)
                        } else {
                            mainViewModel.insertKoord(coordinate)
                        }
                    }
                    Log.d(
                        "MyLog",
                        "KoordFragment:: it.resultCode: ${it.resultCode},  Activity.resultCode: ${Activity.RESULT_OK}"
                    )
                } else {
                    Log.d(
                        "MyLog",
                        "KoordFragment:: it.resultCode: ${it.resultCode},  Activity.resultCode: ${Activity.RESULT_OK}"
                    )
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onCoordResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCoordBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        observer()
        scrollToBottom()
        //  activity?.startForegroundService(Intent(activity, LocationService::class.java))
    }

    private fun observer() {
        mainViewModel.allKoord.observe(viewLifecycleOwner) {
            adapter.setItem(it as MutableList<Coordinate>)
            scrollToBottom()
        }
    }

    private fun initAdapter() {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvKoord.layoutManager = layoutManager
        adapter = CoordAdapter(this@CoordFragment)
        binding.rvKoord.adapter = adapter

        val callback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.rvKoord)

    }

    //Скроллинг в конец списка точек при добавленни точек в адаптер
    private fun scrollToBottom() {
        val lastPosition = adapter.itemCount
        binding.rvKoord.smoothScrollToPosition(lastPosition)
    }

    override fun onClickDelItem(id: Int) {
        mainViewModel.deleteKoord(id)
    }

    override fun onClickCoordinate(koordinate: Coordinate) {
        val intent = Intent(activity, CoordActivity::class.java).apply {
            putExtra(KOORD_KEY, koordinate)
        }
        coordResultLauncher.launch(intent)
    }

    override fun deleteTable() {
        mainViewModel.deleteTable()
    }

    //Расчитываем горизонтальную дистанцию в метрах
    private fun getDistance(
        startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double
    ): Int {
        val arrayDistance: FloatArray = floatArrayOf(0f)
        Location.distanceBetween(
            startLatitude, startLongitude, endLatitude, endLongitude, arrayDistance
        )
        return arrayDistance[0].toInt()
    }


    override fun createExcelTable() {
        val APP_NAME = context?.getString(R.string.app_name)
        val FILE_NAME = APP_NAME + " " + MainTime.getTimeForSaveFile() + ".xls"

        val koordList = adapter.getData()  //Получаем из адаптера список Coordinate

        val wb: Workbook = HSSFWorkbook()
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

        //Проходим циклом создаем и записываем их в соотвтетствующие ячейки и строки
        if (koordList.size > 0) {

            for (i in 0..(koordList.size - 1)) {
                val rowNext = sheet.createRow(i + 1)

                cell = rowNext.createCell(0)
                cell.setCellValue("${i + 1}")

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
            writeExcel?.writeExcel(wb)
            Toast.makeText(
                context?.applicationContext,
                "Файл ${FILE_NAME} сохранен в ${Environment.DIRECTORY_DOCUMENTS}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(context?.applicationContext, "Нет сохраненых точек", Toast.LENGTH_SHORT)
                .show()
            return
        }
    }

    override fun onItemDismiss(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
//        Collections.swap(adapter.getData(), fromPosition, toPosition)
//        Log.d("MyTag", "onItemMove: список до обновлениея позиции ${adapter.getData()}")
//       adapter.notifyItemMoved(fromPosition, toPosition)
//        Log.d("MyTag", "onItemMove: список после обновлениея позиции ${adapter.getData()}")
//       mainViewModel.deleteTable()
//       mainViewModel.insertKoordList(adapter.getData())
      }

    override fun confirmationAction() {
        val dataList = adapter.getData()
        for (i in 0..dataList.size-1){
            dataList[i].distance = 0
        }
        if (dataList.size != 0) {
            mainViewModel.deleteTable()
            for (i in 0..dataList.size - 1) {
                if (i == 0){
                   dataList[i].distance = 0
                   dataList[i].id = i
                    mainViewModel.insertKoord(dataList[i])
               }else{
                    var distance = dataList[i].distance
                    val _distance = getDistance(
                        dataList[i-1].latitude,
                        dataList[i-1].longitude,
                        dataList[i].latitude,
                        dataList[i].longitude
                    )
                    distance += _distance
                    dataList[i].distance = distance
                    dataList[i].id = i
                    Log.d("MyTag", "onCoordResult distance: $distance")

                    mainViewModel.insertKoord(dataList[i])
                }
            }
        }else {
                return
            }
        }



    companion object {
        const val KOORD_KEY = "koord_key"
        const val STATE_KOORD = "koord_state"

        @JvmStatic
        fun newInstance() = CoordFragment()
    }
}