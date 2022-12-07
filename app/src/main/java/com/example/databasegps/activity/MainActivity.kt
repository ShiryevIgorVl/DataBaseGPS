package com.example.databasegps.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.databasegps.R
import com.example.databasegps.databinding.ActivityMainBinding
import com.example.databasegps.entities.ParselKoord
import com.example.databasegps.fragments.FragmentManager
import com.example.databasegps.fragments.KoordFragment
import com.example.databasegps.gps.LocListenerInterfase
import com.example.databasegps.gps.MyLocation
import com.example.databasegps.viewmodel.LocationViewModel


class MainActivity : AppCompatActivity(), LocListenerInterfase {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var myLocation: MyLocation
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    var height = ""


 //   private val locationViewModel: LocationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
     
        setButtonNavListener()

        initGPSService()
        requestPermissionListener()
        chekPermissionGetLocation()

        onClickButtonSave()
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
                        Manifest.permission.ACCESS_COARSE_LOCATION
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
        height = location.altitude.toString()

        // subscriptionLocation(location)

    }


//    override fun subscriptionLocation(location: Location) {
//        locationViewModel.locationLiveData.value = location
//    }

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

    private fun onClickButtonSave() {
        binding.save.setOnClickListener {
            val receivedLocation = ParselKoord(
                latitude = binding.latitude.text.toString(),
                longitude = binding.longitude.text.toString(),
                height = height,
                accuracy = binding.accuracy.text.toString(),
                speed = binding.speed.text.toString()
            )

            val i = Intent(this, KoordActivity::class.java).apply {
                putExtra(KoordActivity.MAIN_KEY, receivedLocation)
            }
            startActivity(i)
        }
    }

}