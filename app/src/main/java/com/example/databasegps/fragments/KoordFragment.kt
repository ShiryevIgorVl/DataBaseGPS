package com.example.databasegps.fragments

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.databasegps.activity.App
import com.example.databasegps.activity.KoordActivity
import com.example.databasegps.databinding.FragmentKoordBinding
import com.example.databasegps.entities.Koordinate
import com.example.databasegps.recyclerview.KoordAdapter
import com.example.databasegps.viewmodel.MainViewModel
import org.apache.commons.compress.harmony.pack200.NewAttributeBands.Call
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List as List1


class KoordFragment : BaseFragment(), KoordAdapter.Listener {

    private lateinit var binding: FragmentKoordBinding
    private lateinit var koordResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: KoordAdapter
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }
    private val koordList = mainViewModel.allKoord as ArrayList<Koordinate>

    val FILE_NAME = "koordDB.csv"
    val DIR_NAME = "MyFiles"

    //
    override fun onClickNew() {
        koordResultLauncher.launch(Intent(activity, KoordActivity::class.java))
    }

    private fun onKoordResult() {
        koordResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    mainViewModel.insertKoord(it.data?.getSerializableExtra(KOORD_KEY) as Koordinate)
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

        onKoordResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKoordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        observer()
    }

    private fun observer() {
        mainViewModel.allKoord.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun initAdapter() = with(binding) {
        rvKoord.layoutManager = LinearLayoutManager(activity)
        adapter = KoordAdapter(this@KoordFragment)
        rvKoord.adapter = adapter
    }

    override fun onClickDelItem(id: Int) {
        mainViewModel.deleteKoord(id)
    }

    fun createExcelTable(){
        val wb: Workbook = HSSFWorkbook()
        var cell: Cell? = null
        var sheet: Sheet? = null

        sheet = wb.createSheet("Новый лист") // Создаем новый лист Excel
        val row:Row = sheet.createRow(0) // Создаем новую строку

        cell = row.createCell(0) //В этой строке создаем новую ячейку
        cell.setCellValue("ID базы данных") //В ячейку пишем значение

        cell = row.createCell(1)
        cell.setCellValue("Наименование")

        cell = row.createCell(2)
        cell.setCellValue("Номер КИП")

        cell = row.createCell(3)
        cell.setCellValue("КИП км")

        cell = row.createCell(4)
        cell.setCellValue("Uтз, В")

        cell = row.createCell(5)
        cell.setCellValue("Uпп, В")

        cell = row.createCell(6)
        cell.setCellValue("Ток поляризации ВЭ, мА")

        cell = row.createCell(7)
        cell.setCellValue("Примечание")

        cell = row.createCell(8)
        cell.setCellValue("Время")

        cell = row.createCell(9)
        cell.setCellValue("Uт-патрон, В")

        cell = row.createCell(10)
        cell.setCellValue("Uпп патрон, В")

        cell = row.createCell(11)
        cell.setCellValue("Ток поляризации" +
                "\n" +
                "ВЭ патрон, мА")

        cell = row.createCell(12)
        cell.setCellValue("Rтп, Ом")

        cell = row.createCell(13)
        cell.setCellValue("Uп-з, Ом")

        cell = row.createCell(14)
        cell.setCellValue("Iпр-с, мА")

        cell = row.createCell(15)
        cell.setCellValue("Глубина, м")

        cell = row.createCell(16)
        cell.setCellValue("Ток в трубе, мА")

        cell = row.createCell(17)
        cell.setCellValue("УЭС, Омхм")

        cell = row.createCell(18)
        cell.setCellValue("Повреждение ИП, м")

        cell = row.createCell(19)
        cell.setCellValue("Широта")

        cell = row.createCell(20)
        cell.setCellValue("Долгота")

        cell = row.createCell(21)
        cell.setCellValue("Высота, м")

        cell = row.createCell(22)
        cell.setCellValue("Точность, м")

        cell = row.createCell(23)
        cell.setCellValue("Скорость, м/с")

       sheet.setColumnWidth(0, (30 * 200))
       sheet.setColumnWidth(1, (30 * 200))
       sheet.setColumnWidth(2, (30 * 200))
       sheet.setColumnWidth(3, (30 * 200))
       sheet.setColumnWidth(4, (30 * 200))
       sheet.setColumnWidth(5, (30 * 200))
       sheet.setColumnWidth(6, (30 * 200))
       sheet.setColumnWidth(7, (30 * 200))
       sheet.setColumnWidth(8, (30 * 200))
       sheet.setColumnWidth(9, (30 * 200))
       sheet.setColumnWidth(10, (30 * 200))
       sheet.setColumnWidth(11, (30 * 200))
       sheet.setColumnWidth(12, (30 * 200))
       sheet.setColumnWidth(13, (30 * 200))
       sheet.setColumnWidth(14, (30 * 200))
       sheet.setColumnWidth(15, (30 * 200))
       sheet.setColumnWidth(16, (30 * 200))
       sheet.setColumnWidth(17, (30 * 200))
       sheet.setColumnWidth(18, (30 * 200))
       sheet.setColumnWidth(19, (30 * 200))
       sheet.setColumnWidth(20, (30 * 200))
       sheet.setColumnWidth(21, (30 * 200))
       sheet.setColumnWidth(22, (30 * 200))
       sheet.setColumnWidth(3, (30 * 200))


    }


    fun writeFile() {
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

        //Проходим циклом по строкам базы и записываем их в соотвтетствующие пеерменные
        for (i in 0..(koordList.size-1)) {
           val id: String = "${koordList[i].id};"
            val name: String = koordList[i].name
            val operationalnumberKIP: String? = koordList[i].operationalnumberKIP
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
        }

    }


    companion object {
        const val KOORD_KEY = "koord_key"

        @JvmStatic
        fun newInstance() = KoordFragment()

    }
}