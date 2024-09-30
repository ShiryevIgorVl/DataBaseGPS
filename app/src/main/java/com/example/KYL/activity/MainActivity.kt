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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.example.KYL.R
import com.example.KYL.constans.Constans
import com.example.KYL.databinding.ActivityMainBinding
import com.example.KYL.fragments.FragmentManager
import com.example.KYL.fragments.CoordFragment
import com.example.KYL.fragments.AllDeleteDialogFragment
import com.example.KYL.fragments.FileNameDialogFragment
import com.example.KYL.gps.LocListenerInterfase
import com.example.KYL.gps.MyLocation
import com.example.KYL.viewmodel.ItemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), LocListenerInterfase {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var myLocation: MyLocation
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fileName: String
    private lateinit var sharedPreferences: SharedPreferences

    private val itemViewModel: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setButtonNavListener()

        initGPSService()
        requestPermissionListener()
        chekPermissionGetLocation()

        FragmentManager.setFragment(CoordFragment.newInstance(), this)

        sharedPreferences = getSharedPreferences(Constans.SPreferences, Context.MODE_PRIVATE)

        itemViewModel.selectedItem.observe(this, Observer { item ->
            title = item
        })
        title = getSP(Constans.SPKey)
    }

    // Создаем в активити верхнее меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Добавляем слушатель нажатий меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> {
                val myDialogFragment = AllDeleteDialogFragment()
                val manager = supportFragmentManager
                val transaction: FragmentTransaction = manager.beginTransaction()
                myDialogFragment.show(transaction, "dialog1")
            }

            R.id.upload -> {

                val dialog = FileNameDialogFragment()

                dialog.onFileNameEntered = { fileName ->

                    // Обработка имени файла, например, сохранение файла с этим именем
                    CoroutineScope(Dispatchers.IO).launch {
                        FragmentManager.currentFragment?.createExcelTable(
                            fileName
                        )
                    }
                }

                dialog.show(supportFragmentManager, "FileNameDialog")
            }

            R.id.download -> {
                FragmentManager.currentFragment?.onActionImport()
            }

            R.id.inYandexMap -> {
                FragmentManager.currentFragment?.openYandexMap()
            }

            R.id.ok -> {
                FragmentManager.currentFragment?.confirmationMoved()
            }

            R.id.up -> {
                FragmentManager.currentFragment?.onClickUp()
            }

            R.id.down -> {
                FragmentManager.currentFragment?.onClickDown()
            }
        }

        return super.onOptionsItemSelected(item)
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
                    Toast.makeText(this, "разрешение на GPS есть", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "разрешение на GPS нет", Toast.LENGTH_SHORT).show()
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
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    //Метод получает локацию при ее изменении и выполняет действия в теле метода
    override fun onGetLocation(location: Location) {
    }

    // слушатель нажатий items Button Navigation View
    private fun setButtonNavListener() {
        binding.bnMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.list -> {
                    FragmentManager.setFragment(CoordFragment.newInstance(), this)
                }

                R.id.save -> {
                    FragmentManager.currentFragment?.onClickNew()
                }
            }
            true
        }
    }

    //Сохранение настроек имени title в файл
    private fun editorSP(key: String, item: String) {
        val editor = sharedPreferences.edit()
        if (item != "") {
            editor.putString(key, item)
        }
    }

    private fun getSP(key: String): String? {
        val name = sharedPreferences.getString(key, "")
        Log.d("MyTAG", "getSP: $name")
        return name
    }

}