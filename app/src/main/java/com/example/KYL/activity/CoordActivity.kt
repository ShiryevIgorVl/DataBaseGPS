package com.example.KYL.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
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
import com.example.KYL.constans.Time
import com.example.KYL.databinding.ActivityCoordBinding
import com.example.KYL.entities.Coordinate
import com.example.KYL.fragments.CoordFragment
import com.example.KYL.gps.LocListenerInterfase
import com.example.KYL.gps.MyLocation
import java.util.*

class CoordActivity : AppCompatActivity(), LocListenerInterfase {
    private lateinit var binding: ActivityCoordBinding

    private lateinit var locationManager: LocationManager
    private lateinit var myLocation: MyLocation

    private lateinit var height: String
    private lateinit var speed: String
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var accuracy: String

    private lateinit var loc: Location

    private lateinit var decimalFormat: DecimalFormat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGPSService()
        actionBarSetting()

        chekPermissionGetLocation()

        onClickKoordPointBotton()
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
            twLat.text = formatTV(location.latitude)
            tvLon.text = formatTV(location.longitude)
            tvAcc.text = formatTVAcc(location.accuracy)
        }

        speed = formatTVAcc(location.speed)
        height = formatTVAlt(location.altitude)
        longitude = formatTV(location.longitude)
        latitude = formatTV(location.latitude)
        accuracy = formatTVAcc(location.accuracy)
    }

    // Подключение стрелки назад в акшен баре, id этой стрелки home см. функцию onOptionsItemSelected
    private fun actionBarSetting() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //Передаем данные в KoordFragment из окошек заполнения
    private fun setMainResult() {
        val onCreateKoordinate = onCreateKoordinate()
        val i = Intent(this, MainActivity::class.java).apply {
            putExtra(CoordFragment.KOORD_KEY, onCreateKoordinate)
        }
        Log.d("MyLog", "setMainResult: ${i.extras.toString()}")
        setResult(RESULT_OK, i)
    }


    // Функция заполнения класса Koordinate()
    private fun onCreateKoordinate(): Coordinate {
        lateinit var coordinate: Coordinate

        coordinate = Coordinate(
            null,
            name = binding.tvKoordName.text.toString(),
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
            time = Time.getTime(),
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

    private fun onClickKoordPointBotton(){
        binding.btKIP.setOnClickListener {
            binding.tvKoordName.text = binding.btKIP.text
        }

        binding.btKontPoint.setOnClickListener {
            binding.tvKoordName.text = binding.btKontPoint.text
        }
    }

    private fun formatTV(a: Double): String{
        decimalFormat = DecimalFormat("##.######")
        return decimalFormat.format(a).toString()
    }

    private fun formatTVAlt(a: Double): String{
        decimalFormat = DecimalFormat("##.#")
        return decimalFormat.format(a).toString()
    }
    private fun formatTVAcc(a: Float): String{
        decimalFormat = DecimalFormat("##.#")
        return decimalFormat.format(a).toString()
    }



    companion object {
        const val MAIN_KEY = "main_key"
    }
}