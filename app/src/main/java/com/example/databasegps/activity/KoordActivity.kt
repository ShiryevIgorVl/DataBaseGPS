package com.example.databasegps.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import com.example.databasegps.R
import com.example.databasegps.databinding.ActivityKoodBinding
import com.example.databasegps.entities.Koordinate
import com.example.databasegps.entities.ParselKoord
import com.example.databasegps.fragments.FragmentManager
import com.example.databasegps.fragments.KoordFragment
import com.example.databasegps.gps.LocListenerInterfase
import com.example.databasegps.gps.MyLocation
import java.util.*

class KoordActivity : AppCompatActivity(), LocListenerInterfase {
    private lateinit var binding: ActivityKoodBinding

    private lateinit var locationManager: LocationManager
    private lateinit var myLocation: MyLocation

    private lateinit var height: String
    private lateinit var speed: String
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var accuracy: String

    private lateinit var loc: Location


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGPSService()
        actionBarSetting()

        chekPermissionGetLocation()
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

    override fun onGetLocation(location: Location) {
        binding.twLat.text = location.latitude.toString()
        binding.tvLon.text = location.longitude.toString()
        binding.tvAcc.text = location.accuracy.toString()

        speed = location.speed.toString()
        height = location.altitude.toString()
        longitude = location.longitude.toString()
        latitude = location.latitude.toString()
        accuracy = location.accuracy.toString()
    }

    // Подключение стрелки назад в акшен баре, id этой стрелки home см. функцию onOptionsItemSelected
    private fun actionBarSetting() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //Передаем данные в KoordFragment из окошек заполнения
    private fun setMainResult() {
        val onCreateKoordinate = onCreateKoordinate()
        Log.d("MyLog", "setMainResult: ${onCreateKoordinate.id}")
        Log.d("MyLog", "setMainResult: ${onCreateKoordinate.name}")
        Log.d("MyLog", "setMainResult: ${onCreateKoordinate.latitude}")
        Log.d("MyLog", "setMainResult: ${onCreateKoordinate.longitude}")
        Log.d("MyLog", "setMainResult: ${onCreateKoordinate.height}")
        Log.d("MyLog", "setMainResult: ${onCreateKoordinate.accuracy}")
        Log.d("MyLog", "setMainResult: ${onCreateKoordinate.speed}")
        Log.d("MyLog", "setMainResult: ${onCreateKoordinate.note}")

        val i = Intent(this, MainActivity::class.java).apply {
            putExtra(KoordFragment.KOORD_KEY, onCreateKoordinate)
        }
        Log.d("MyLog", "setMainResult: ${i.extras.toString()}")
        setResult(RESULT_OK, i)

    }


    // Функция заполнения класса Koordinate()
    private fun onCreateKoordinate(): Koordinate {
        lateinit var koordinate: Koordinate

        koordinate = Koordinate(
            null,
            name = binding.btKontPoint.text.toString(),
            latitude = latitude,
            longitude = longitude,
            height = height,
            accuracy = accuracy,
            speed = speed,
            note = binding.etNote.text.toString()
        )

        Log.d("MyLog", "onCreateKoordinate1: ${koordinate.latitude}")
        Log.d("MyLog", "onCreateKoordinate2: ${koordinate.longitude}")
        Log.d("MyLog", "onCreateKoordinate3: ${koordinate.height}")
        Log.d("MyLog", "onCreateKoordinate4: ${koordinate.accuracy}")
        Log.d("MyLog", "onCreateKoordinate5: ${koordinate.speed}")
        return koordinate
    }

    // Функция получения текущего времени
    private fun getTime(): String {
        val formatter = SimpleDateFormat("hh:mm:ss dd.mm.yyyy", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    companion object {
        const val MAIN_KEY = "main_key"
    }
}