package com.example.KYL.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.KYL.R
import com.example.KYL.constans.MainDecimalFormat
import com.example.KYL.constans.MainTime
import com.example.KYL.database.MainDataBase_Impl
import com.example.KYL.databinding.ActivityCoordBinding
import com.example.KYL.entities.Coordinate
import com.example.KYL.fragments.CoordFragment
import com.example.KYL.gps.LocListenerInterfase
import com.example.KYL.gps.MyLocation
import dalvik.system.DelegateLastClassLoader


class CoordActivity() : AppCompatActivity(), LocListenerInterfase {
    private lateinit var binding: ActivityCoordBinding

    //    private lateinit var sharedPreferences: SharedPreferences
//    val SHARED_NAME = "sharedName"
//    val SHARED_LATITUDE = "sharedLatitude"
//    val SHARED_LONGITUDE = "sharedLongitude"
    var _latitude: Double = 0.0
    var _longitude: Double = 0.0
//    private var distanceFirst: Float = 0f
//    private var distanceSecond: Float = 0f
//    private var distanceResult: Float = 0f

    private lateinit var locationManager: LocationManager
    private lateinit var myLocation: MyLocation

    private lateinit var height: String
    private lateinit var speed: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var accuracy: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGPSService()
        actionBarSetting()
//        initSharedPref()

        chekPermissionGetLocation()

        onClickKoordPointBotton()
    }
//
//    private fun initSharedPref() {
//        sharedPreferences = getSharedPreferences(SHARED_NAME, MODE_PRIVATE)
//    }
//
//    fun setLatitudeSP(d: Double) {
//        val spEditor = sharedPreferences.edit()
//        spEditor.putFloat(SHARED_LATITUDE, d.toFloat())
//        spEditor.apply()
//    }
//
//    fun setLongitudeSP(d: Double) {
//        val spEditor = sharedPreferences.edit()
//        spEditor.putFloat(SHARED_LONGITUDE, d.toFloat())
//        spEditor.apply()
//    }
//
//    private fun getLatitudeSP(): Float {
//        return sharedPreferences.getFloat(SHARED_LATITUDE, 0f)
//    }
//
//    private fun getLongitudeSP(): Float {
//        return sharedPreferences.getFloat(SHARED_LONGITUDE, 0f)
//    }

    //Получам из БД список объектов Coordinate
//    private fun getCoordListInDB(): List<Coordinate> {
//        return dao.getAllKoordinateList()
//    }
//
//    //Получение последненй записи из DB
//    private fun getLastCoord(): Coordinate {
//        return dao.getLastCoordinate()
//    }

    //Получаем из списка по позиции широту
    private fun getLat(position: Int, coorList: List<Coordinate>): Double {
        return coorList[position].latitude.toDouble()
    }

    //Получаем из списка по позиции долготу
    private fun getLong(position: Int, coorList: List<Coordinate>): Double {
        return coorList[position].longitude.toDouble()
    }

    //Расчитываем горизонтальную дистанцию
    private fun getDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float {
        val arrayDistance: FloatArray = floatArrayOf(0f)
        Location.distanceBetween(
            startLatitude,
            startLongitude,
            endLatitude,
            endLongitude,
            arrayDistance
        )
        return arrayDistance[0]
    }


    // Создаем в активити верхнее меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.koord_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Добавляем слушатель нажатий меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.km_save) {
//            val arrayDistance: FloatArray = floatArrayOf(0f)
//            Location.distanceBetween(
//                getLatitudeSP().toDouble(),
//                getLongitudeSP().toDouble(),
//                _latitude,
//                _longitude,
//                arrayDistance
//            )
//            distanceSecond = distanceFirst
//            setLatitudeSP(_latitude)
//            setLongitudeSP(_longitude)
//            distanceFirst = arrayDistance[0]
//            distanceResult = distanceFirst + distanceSecond
            // getLastCoord()
            // Log.d("MyTag", "onOptionsItemSelected: ${getLastCoord()}")
            setMainResult()
            finish()
        } else if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    //Инициализируем менеджер локациии и подключаем setLocListenerInterface у классу MyLocatiion
    private fun initGPSService() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        myLocation = MyLocation()
        myLocation.setLocListenerInterface(this)
    }

    //Проверка наличия разрешений на локацию и подключение обновлений данных локации (без проверки не работает)
    private fun chekPermissionGetLocation() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    2f,
                    myLocation
                )
            }
            else -> {
                Toast.makeText(this, "Нет разрешения GPS", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Заполнение полей и переменных данными из сервиса геолокации
    override fun onGetLocation(location: Location) {
        binding.apply {
            twLat.text = MainDecimalFormat.formatTV(location.latitude)
            tvLon.text = MainDecimalFormat.formatTV(location.longitude)
            tvAcc.text = MainDecimalFormat.formatTVAcc(location.accuracy)
        }

        speed = MainDecimalFormat.formatTVAcc(location.speed)
        height = MainDecimalFormat.formatTVAlt(location.altitude)
        longitude = location.longitude
        latitude = location.latitude
        accuracy = MainDecimalFormat.formatTVAcc(location.accuracy)

        _latitude = location.latitude
        _longitude = location.longitude
    }

    // Подключение стрелки назад в акшен баре, id этой стрелки home см. функцию onOptionsItemSelected
    private fun actionBarSetting() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //Передаем данные в CoordFragment из окошек заполнения
    private fun setMainResult() {
        val onCreateCoordinate = onCreateCoordinate()
        val i = Intent(this, MainActivity::class.java).apply {
            putExtra(CoordFragment.KOORD_KEY, onCreateCoordinate)
        }
        Log.d("MyLog", "setMainResult: ${i.extras.toString()}")
        setResult(RESULT_OK, i)
    }


    // Функция заполнения класса Coordinate()
    private fun onCreateCoordinate(): Coordinate {
        lateinit var coordinate: Coordinate

        coordinate = Coordinate(
            id = null,
            name = binding.tvKoordName.text.toString(),
            distance = 0f,
            latitude = latitude,
            longitude = longitude,
            height = height,
            accuracy = accuracy,
            speed = speed,
            note = binding.etNote.text.toString(),
            operationalnumberKIP = binding.etOperationalnumberKIP.text.toString(),
            operationalKM = binding.etOperationalKM.text.toString(),
            utsPipe = binding.etUtsPipe.text.toString(),
            uppPipe = binding.etUppPipe.text.toString(),
            ipolPipe = binding.etiPolPipe.text.toString(),
            time = MainTime.getTime(),
            utsСover = binding.etUtsOver.text.toString(),
            uppCover = binding.etUppCover.text.toString(),
            ipolCover = binding.etIpolCover.text.toString(),
            rPipeCover = binding.etRPipeCover.text.toString(),
            ups = binding.etUps.text.toString(),
            iprot = binding.etIprot.text.toString(),
            depthPipe = binding.etDepthPipe.text.toString(),
            iPipe = binding.etIPipe.text.toString(),
            ues = binding.etUES.text.toString(),
            damageIP = binding.etDamageIP.text.toString()
        )
        return coordinate
    }

    private fun onClickKoordPointBotton() {
        binding.btKIP.setOnClickListener {
            binding.tvKoordName.text = "КИП"
        }

        binding.btKontPoint.setOnClickListener {
            binding.tvKoordName.text = "Контрольная точка"
        }

        binding.btAD.setOnClickListener {
            binding.tvKoordName.text = "Автомобильная дорога ( м)"
        }

        binding.btRiver.setOnClickListener {
            binding.tvKoordName.text = "Река ( м)"
        }
    }
}