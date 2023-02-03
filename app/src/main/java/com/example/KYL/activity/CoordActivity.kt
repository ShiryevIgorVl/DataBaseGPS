package com.example.KYL.activity

import android.Manifest
import android.content.Context
import android.content.Intent
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
import com.example.KYL.databinding.ActivityCoordBinding
import com.example.KYL.entities.Coordinate
import com.example.KYL.fragments.CoordFragment
import com.example.KYL.gps.LocListenerInterfase
import com.example.KYL.gps.MyLocation

class CoordActivity() : AppCompatActivity(), LocListenerInterfase {
    private lateinit var binding: ActivityCoordBinding

    var _latitude: Double = 0.0
    var _longitude: Double = 0.0

    private lateinit var locationManager: LocationManager
    private lateinit var myLocation: MyLocation

    private lateinit var height: String
    private lateinit var speed: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var accuracy: String

    private var coordinate: Coordinate? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGPSService()
        actionBarSetting()

        chekPermissionGetLocation()

        getCoordinate()
        onClickKoordPointBotton()
    }

    //Получаем Coordinate для редакирования из CoordFragment
    private fun getCoordinate() {
        val serializableCoordinate = intent.getSerializableExtra(CoordFragment.KOORD_KEY)
        if (serializableCoordinate != null) {
            coordinate = serializableCoordinate as Coordinate
            fillCoordinate()
        }
    }

    //Записываем из переданного для редактирования Coordinate данные в EditText этого активити
    private fun fillCoordinate() = with(binding) {
        tvKoordName.text = coordinate?.name
        etUtsPipe.setText(coordinate?.utsPipe)   //Именно так "setText" по другому не работает так как EditText
        etUppPipe.setText(coordinate?.uppPipe)
        etiPolPipe.setText(coordinate?.ipolPipe)
        etUtsOver.setText(coordinate?.utsСover)
        etUppCover.setText(coordinate?.uppCover)
        etIpolCover.setText(coordinate?.ipolCover)
        etRPipeCover.setText(coordinate?.rPipeCover)
        etIprot.setText(coordinate?.iprot)
        etUps.setText(coordinate?.ups)
        etDepthPipe.setText(coordinate?.depthPipe)
        etIPipe.setText(coordinate?.iPipe)
        etUES.setText(coordinate?.ues)
        etDamageIP.setText(coordinate?.damageIP)
        etOperationalnumberKIP.setText(coordinate?.operationalnumberKIP)
        etOperationalKM.setText(coordinate?.operationalKM)
        etNote.setText(coordinate?.note)
    }

    // Создаем в активити верхнее меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.koord_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Добавляем слушатель нажатий меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.km_save) {
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

    //Передаем данные в CoordFragment из окошек заполнения для записи в DB
    private fun setMainResult() {
        var editState = "new"

        val tempCoordinate: Coordinate?
        if (coordinate == null) {
            tempCoordinate = onCreateCoordinate()
        } else {
            tempCoordinate = updateCootdinate()
            editState = "update"
        }
        val i = Intent(this, MainActivity::class.java).apply {
            putExtra(CoordFragment.KOORD_KEY, tempCoordinate)
            putExtra(CoordFragment.STATE_KOORD, editState)
        }
        Log.d("MyLog", "setMainResult: ${i.extras.toString()}")
        setResult(RESULT_OK, i)
    }

    //Передаем данные в CoordFragment из окошек заполнения для редактирования записей в DB
    private fun updateCootdinate(): Coordinate? {
        return coordinate?.copy(
            name = binding.tvKoordName.text.toString(),
            note = binding.etNote.text.toString(),
            operationalnumberKIP = binding.etOperationalnumberKIP.text.toString(),
            operationalKM = binding.etOperationalKM.text.toString(),
            utsPipe = binding.etUtsPipe.text.toString(),
            uppPipe = binding.etUppPipe.text.toString(),
            ipolPipe = binding.etiPolPipe.text.toString(),
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
    }

    // Функция заполнения класса Coordinate()
    private fun onCreateCoordinate(): Coordinate {
        return Coordinate(
            id = null,
            name = binding.tvKoordName.text.toString(),
            distance = 0,
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