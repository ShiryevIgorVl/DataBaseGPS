package com.example.KYL.fragments

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.KYL.R
import com.example.KYL.activity.App
import com.example.KYL.activity.CoordActivity
import com.example.KYL.constans.MainTime
import com.example.KYL.databinding.FragmentCoordBinding
import com.example.KYL.entities.Coordinate
import com.example.KYL.recyclerview.CoordAdapter

import com.example.KYL.recyclerview.ItemTouchHelperCallback

import com.example.KYL.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CoordFragment : BaseFragment(), CoordAdapter.Listener {

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
//                            Log.d("MyLog", "onCoordResult distance: $distance")
//                            Log.d("MyLog", "onCoordResult coordinate: $coordinate")
                            mainViewModel.insertKoord(coordinate)
                        } else {
                            mainViewModel.insertKoord(coordinate)
                        }
                    }
                    //              Log.d(
                    //                "MyLog",
                    //              "KoordFragment:: it.resultCode: ${it.resultCode},  Activity.resultCode: ${Activity.RESULT_OK}"
                    //          )
                } else {
                    //                Log.d(
                    //                  "MyLog",
                    //                "KoordFragment:: it.resultCode: ${it.resultCode},  Activity.resultCode: ${Activity.RESULT_OK}"
                    //          )
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

    //Расчитываем горизонтальную дистанцию в метрах и округляем в большую сторону до четных значений
    private fun getDistance(
        startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double
    ): Int {
        val arrayDistance: FloatArray = floatArrayOf(0f)
        val _distance: Int
        Location.distanceBetween(
            startLatitude, startLongitude, endLatitude, endLongitude, arrayDistance
        )
        val distance: Int = arrayDistance[0].toInt()
        if (distance % 2 != 0) {
            _distance = distance + 1
        } else _distance = distance
        return _distance
    }

    //Создаем и записываем файл Excel
    override suspend fun createExcelTable() {
        val APP_NAME = context?.getString(R.string.app_name)
        val FILE_NAME = APP_NAME + " " + MainTime.getTimeForSaveFile() + ".xlsx"

        val koordList = adapter.getData()  //Получаем из адаптера список Coordinate
        if (koordList.size > 0) {

            mainViewModel.createExcleTable(koordList, APP_NAME!!)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context?.applicationContext,
                    "Файл ${FILE_NAME} сохранен в ${Environment.DIRECTORY_DOCUMENTS}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context?.applicationContext,
                    "Нет сохраненых точек",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            return
        }
    }

    // Обновляем расчет дистанции и Coordinate.id после перемещения Items в RecyclerView и перезаписываем DB
    override fun confirmationAction() {
        val dataList = adapter.getData()
        for (i in 0..dataList.size - 1) {
            dataList[i].distance = 0
        }
        if (dataList.size != 0) {
            mainViewModel.deleteTable()
            for (i in 0..dataList.size - 1) {
                if (i == 0) {
                    dataList[i].distance = 0
                    dataList[i].id = i
                    mainViewModel.insertKoord(dataList[i])
                } else {
                    var distance = dataList[i - 1].distance
                    val _distance = getDistance(
                        dataList[i - 1].latitude,
                        dataList[i - 1].longitude,
                        dataList[i].latitude,
                        dataList[i].longitude
                    )
                    distance += _distance
                    dataList[i].distance = distance
                    dataList[i].id = i
//                    Log.d("MyTag", "onCoordResult distance: $distance")

                    mainViewModel.insertKoord(dataList[i])
                }
            }
        } else {
            return
        }
    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // получаем  Uri из intent
                val intent = result.data
                val uri = intent?.data!!
                // запускаем чтение файла .xlsx по полученному Uri
                context?.let { mainViewModel.importDataBase(uri, it.applicationContext) }
            }
        }

    override fun onActionImport()  {


        // настраиваем фильтры intent
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        // запускаем контракт
        startForResult.launch(intent)

    }

    companion object {
        const val KOORD_KEY = "koord_key"
        const val STATE_KOORD = "koord_state"

        @JvmStatic
        fun newInstance() = CoordFragment()
    }
}