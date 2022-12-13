package com.example.databasegps.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.databasegps.R
import com.example.databasegps.databinding.ActivityMainBinding
import com.example.databasegps.fragments.FragmentManager
import com.example.databasegps.fragments.KoordFragment
import com.example.databasegps.gps.LocListenerInterfase
import com.example.databasegps.gps.MyLocation


class MainActivity : AppCompatActivity(), LocListenerInterfase {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var myLocation: MyLocation
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    //   private val locationViewModel: LocationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    //Чтобы обойти проблемы с поиском подходящего анализатора XML добовляем эти настройки
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )

        setButtonNavListener()

        initGPSService()
        requestPermissionListener()
        chekPermissionGetLocation()

        FragmentManager.setFragment(KoordFragment.newInstance(), this)
    }

    //Инициализируем менеджер локациии и подключаем setLocListenerInterface у классу MyLocatiion
    private fun initGPSService() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        myLocation = MyLocation()
        myLocation.setLocListenerInterface(this)
    }

    // Проверка разрешений для GPS и иннициализация запускателя registerForActivityResult в соответсвии с рекомедациями
    private fun requestPermissionListener() {
        pLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    Toast.makeText(this, "разрениние на GPS есть", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "разрениние на GPS нет", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Еще одна проверка разрешений для GPS
    // и запуск GPS если есть (иначе в другом блоке не запустится)
    // или запуск диалога с разрешениями
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
                pLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    //Метод получает локацию при ее изменении и выполняет действия в теле метода
    override fun onGetLocation(location: Location) {
        binding.latitude.text = location.latitude.toString()
        binding.longitude.text = location.longitude.toString()
        binding.speed.text = location.speed.toString()
        binding.accuracy.text = location.accuracy.toString()
    }

    // слушатель нажатий items Button Navigation View
    private fun setButtonNavListener() {
        binding.bnMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    Log.d("MyLog", "нажали settings")
                }
                R.id.list -> {
                    FragmentManager.setFragment(KoordFragment.newInstance(), this)
                }
                R.id.save -> {
                    FragmentManager.currentFragment?.onClickNew()
                }
                R.id.upload -> {
                    Log.d("MyLog", "нажали upload")
                }
            }
            true
        }
    }
}