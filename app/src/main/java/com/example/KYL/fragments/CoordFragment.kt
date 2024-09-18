package com.example.KYL.fragments

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
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
import com.example.KYL.entities.CoordinateLatLongName
import com.example.KYL.recyclerview.CoordAdapter

import com.example.KYL.recyclerview.ItemTouchHelperCallback

import com.example.KYL.viewmodel.MainViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class CoordFragment : BaseFragment(), CoordAdapter.Listener {

    private lateinit var binding: FragmentCoordBinding
    private lateinit var coordResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: CoordAdapter

    private val listCoordinateLatLongName: MutableList<CoordinateLatLongName> = arrayListOf()


    //  private lateinit var layoutManager: LayoutManager
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

                    if (editState == "update") {
                        mainViewModel.updateKoord(coordinate)
                    } else {
                        if (koordList.isNotEmpty()) {
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

        //getAllListCoordinate()
        initAdapter()
        observer()
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
        layoutManager.stackFromEnd = true

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
        val buttonDeleteDialogFragment = ButtonDeleteDialogFragment {
            mainViewModel.deleteKoord(id)

            val dataList = adapter.getData() as MutableList
            dataList.remove(dataList[id])

            updateDistance(dataList)
        }
        childFragmentManager?.let {
            buttonDeleteDialogFragment.show(
                it,
                "buttonDeleteDialogFragment"
            )
        }
    }

    override fun onClickToMapItem(id: Int) {
        val dataList = adapter.getData()
        openYandexMapWithMarker(dataList[id].latitude.toFloat(), dataList[id].longitude.toFloat())
    }

    override fun deleteButton(id: Int) {
        TODO()
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
        if (koordList.isNotEmpty()) {

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

    override fun onClickUp() {
        val firstPosition = 0
        binding.rvKoord.smoothScrollToPosition(firstPosition)
    }

    override fun onClickDown() {
        scrollToBottom()
    }

    //confirmationMoved() визуально сработает только при пересоздании фрагмента
    override fun confirmationMoved() {
        val listAdapter = adapter.getData()
//        Log.d("Mytag", "oconfirmationMoved listAdapter: ${listAdapter.size}")

        if (listAdapter.isNotEmpty()) {
            for (i in 0..listAdapter.size - 1) {
                listAdapter[i].id = i
                listAdapter[i].distance = 0
            }

            for (i in 0..listAdapter.size - 1) {
                if (i == 0) {
                    listAdapter[i].distance = 0
                    mainViewModel.updateKoord(listAdapter[i])
                } else {
                    var distance = listAdapter[i - 1].distance
                    val _distance = getDistance(
                        listAdapter[i - 1].latitude,
                        listAdapter[i - 1].longitude,
                        listAdapter[i].latitude,
                        listAdapter[i].longitude
                    )
                    distance += _distance
                    listAdapter[i].distance = distance

                    mainViewModel.updateKoord(listAdapter[i])
                }
            }
        }
    }

    private fun updateDistance(dataList: MutableList<Coordinate>) {
        if (dataList.isNotEmpty()) {
            for (i in 0..dataList.size - 1) {
                if (i == 0) {
                    dataList[i].distance = 0
                    mainViewModel.updateKoord(dataList[i])
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
                    mainViewModel.updateKoord(dataList[i])
                }
            }
        } else {
            return
        }
    }

    // Обновляем расчет дистанции и Coordinate.id после перемещения Items в RecyclerView и перезаписываем DB
    override fun confirmationAction() {
        val dataList = adapter.getData()
//        Log.d("MyTag", "onCoordResult id: ${dataList}")
//        for (i in 0..dataList.size - 1) {
//            dataList[i].distance = 0
//        }
        if (dataList.isNotEmpty()) {
            for (i in 0..dataList.size - 1) {
                if (i == 0) {
                    dataList[i].distance = 0
                    mainViewModel.updateKoord(dataList[i])
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
//                    Log.d("MyTag", "onCoordResult id: ${dataList[i].id}")

                    mainViewModel.updateKoord(dataList[i])
                }
            }
        } else {
            return
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // получаем  Uri из intent
                val intent = result.data
                val uri = intent?.data!!
                // запускаем чтение файла .xlsx по полученному Uri
                context?.let { mainViewModel.importDataBase(uri, it.applicationContext) }
            }
        }

    override fun onActionImport() {
        // настраиваем фильтры intent
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        // запускаем контракт
        startForResult.launch(intent)
    }

    private fun getAllListCoordinate() {
        val coordinateFlow = mainViewModel.getALLCoordinate()
        coordinateFlow.map {
            Log.d("MyTag", "getAllListCoordinate: $it")
        }
    }

//    override fun openYandexMap() {
//        val coordinateList = adapter.getData()
//        //for (i in 0..coordinateList.size-1){
//        openYandexMapWithMarker(
//            coordinateList[0].latitude,
//            coordinateList[0].longitude,
//            coordinateList[0].name
//        )
//        // }
//    }

    //Делаем список точек для экспорта в карты
    private fun createCoordinateLatLongNameList(): List<CoordinateLatLongName> {
        val coodinateList = adapter.getData()
        for (i in 0..coodinateList.size - 1) {
            CoordinateLatLongName(
                longitude = coodinateList[i].longitude,
                latitude = coodinateList[i].latitude,
                name = coodinateList[i].name
            )
            listCoordinateLatLongName.add(CoordinateLatLongName())
        }
        return listCoordinateLatLongName
    }

    //Трансформируем список точек для экспорта в GSON
    private fun coordinateListToGson(coodinateList: List<CoordinateLatLongName>): String {
        val gsonPoints = Gson().toJson(coodinateList)
        return gsonPoints
    }


    //Метод для экспорта одной точки в карты
    // "yandexmaps://maps.yandex.ru/?pt=37.673098,55.757134&z=12&l=map"
    //"geo:$longitude,$latitude.3?z=11"
    //"yandexmaps://maps.yandex.ru/?ll=$longitude,$latitude&z=12&pt=$longitude,$latitude,pm2rdl~$name"

    private fun openYandexMapWithMarker(latitude: Float, longitude: Float) {
        // Формируем URI для открытия Яндекс.Карт с меткой
        val uri =
            Uri.parse("yandexmaps://maps.yandex.ru/?pt=$longitude,$latitude,&z=12&l=skl")

        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    companion object {
        const val KOORD_KEY = "koord_key"
        const val STATE_KOORD = "koord_state"

        @JvmStatic
        fun newInstance() = CoordFragment()
    }
}