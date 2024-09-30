package com.example.KYL.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.KYL.R
import com.example.KYL.constans.MainDecimalFormat
import com.example.KYL.constans.MainTime
import com.example.KYL.databinding.ActivityCoordBinding
import com.example.KYL.entities.Coordinate
import com.example.KYL.fragments.CoordFragment
import com.example.KYL.gps.LocListenerInterfase
import com.example.KYL.gps.MyLocation
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CoordActivity : AppCompatActivity(), LocListenerInterfase {
    private lateinit var binding: ActivityCoordBinding

    var _latitude: Double = 0.0
    var _longitude: Double = 0.0

    private lateinit var locationManager: LocationManager
    private lateinit var myLocation: MyLocation

    private var height: String = "0.0"
    private var speed: String = "0.0"
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private  var accuracy: String = "0.0"

    private var coordinate: Coordinate? = null

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private var imageUri: Uri? = null
    private var tempImageFile: File? = null

    private var imageFile: File? = null

    private var imageFileName = ""
    private var counter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.title_coordactivity)
        initGPSService()
        actionBarSetting()

        registerPermissionCamera()

        checkPermissionCamera()
        chekPermissionGetLocation()

        getCoordinate()
        onClickKoordPointBotton()

        getPhoto()

    }

    //Получаем Coordinate для редактирования из CoordFragment
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
        etUtsPipe1.editText?.setText(coordinate?.utsPipe)   //Именно так "setText" по другому не работает так как EditText
        etUppPipe1.editText?.setText(coordinate?.uppPipe)
        etiPolPipe1.editText?.setText(coordinate?.ipolPipe)
        etUtsOver.editText?.setText(coordinate?.utsСover)
        etUppCover.editText?.setText(coordinate?.uppCover)
        etIpolCover.editText?.setText(coordinate?.ipolCover)
        etRPipeCover.editText?.setText(coordinate?.rPipeCover)
        etIprot.editText?.setText(coordinate?.iprot)
        etUps.editText?.setText(coordinate?.ups)
        etDepthPipe.editText?.setText(coordinate?.depthPipe)
        etIPipe.editText?.setText(coordinate?.iPipe)
        etUES.editText?.setText(coordinate?.ues)
        etDamageIP.editText?.setText(coordinate?.damageIP)
        etOperationalnumberKIP.editText?.setText(coordinate?.operationalnumberKIP)
        etOperationalKM.editText?.setText(coordinate?.operationalKM)
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
            tvLat.text = MainDecimalFormat.formatTV(location.latitude)
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
        //      Log.d("MyLog", "setMainResult: ${i.extras.toString()}")
        setResult(RESULT_OK, i)
    }

    //Передаем данные в CoordFragment из окошек заполнения для редактирования записей в DB
    private fun updateCootdinate(): Coordinate? {
        return coordinate?.copy(
            name = binding.tvKoordName.text.toString(),
            note = binding.etNote.text.toString(),
            operationalnumberKIP = binding.etOperationalnumberKIP.editText?.text.toString(),
            operationalKM = binding.etOperationalKM.editText?.text.toString(),
            utsPipe = binding.etUtsPipe1.editText?.text.toString(),
            uppPipe = binding.etUppPipe1.editText?.text.toString(),
            ipolPipe = binding.etiPolPipe1.editText?.text.toString(),
            utsСover = binding.etUtsOver.editText?.text.toString(),
            uppCover = binding.etUppCover.editText?.text.toString(),
            ipolCover = binding.etIpolCover.editText?.text.toString(),
            rPipeCover = binding.etRPipeCover.editText?.text.toString(),
            ups = binding.etUps.editText?.text.toString(),
            iprot = binding.etIprot.editText?.text.toString(),
            depthPipe = binding.etDepthPipe.editText?.text.toString(),
            iPipe = binding.etIPipe.editText?.text.toString(),
            ues = binding.etUES.editText?.text.toString(),
            damageIP = binding.etDamageIP.editText?.text.toString()
        )
    }

    // Функция заполнения класса Coordinate()
    private fun onCreateCoordinate(): Coordinate {
        return Coordinate(
            id = 0,
            name = binding.tvKoordName.text.toString(),
            distance = 0,
            latitude = latitude,
            longitude = longitude,
            height = height,
            accuracy = accuracy,
            speed = speed,
            note = binding.etNote.text.toString(),
            operationalnumberKIP = binding.etOperationalnumberKIP.editText?.text.toString(),
            operationalKM = binding.etOperationalKM.editText?.text.toString(),
            utsPipe = binding.etUtsPipe1.editText?.text.toString(),
            uppPipe = binding.etUppPipe1.editText?.text.toString(),
            ipolPipe = binding.etiPolPipe1.editText?.text.toString(),
            time = MainTime.getTime(),
            utsСover = binding.etUtsOver.editText?.text.toString(),
            uppCover = binding.etUppCover.editText?.text.toString(),
            ipolCover = binding.etIpolCover.editText?.text.toString(),
            rPipeCover = binding.etRPipeCover.editText?.text.toString(),
            ups = binding.etUps.editText?.text.toString(),
            iprot = binding.etIprot.editText?.text.toString(),
            depthPipe = binding.etDepthPipe.editText?.text.toString(),
            iPipe = binding.etIPipe.editText?.text.toString(),
            ues = binding.etUES.editText?.text.toString(),
            damageIP = binding.etDamageIP.editText?.text.toString()
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
            binding.tvKoordName.text = "Автомобильная дорога (6 м)"
        }

        binding.btRiver.setOnClickListener {
            binding.tvKoordName.text = "Река (4 м)"
        }

        binding.btAngleRotation.setOnClickListener {
            binding.tvKoordName.text = "Угол поворота"
        }

        binding.btDamageIp.setOnClickListener {
            binding.tvKoordName.text = "Повреждение изоляции"
        }

        binding.btKmIndicator.setOnClickListener {
            binding.tvKoordName.text = "Указатель километровый"
        }

        binding.btLnearCrane.setOnClickListener {
            binding.tvKoordName.text = "Линейный кран №"
        }
    }

    private fun getPhoto() {
        binding.fabPhoto.setOnClickListener {
            if (checkPermissionCamera()) {
                startCamera()
            } else {
                Toast.makeText(this, "Не получено разрешение для камеры", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        val imageFile: File? = createTempImageFile()
        imageFile?.let {
            imageUri =
                FileProvider.getUriForFile(
                    this,
                    "com.example.kyl.fileprovider",
                    it
                )
            fotoResul.launch(imageUri)
        }
    }

    private fun checkPermissionCamera(): Boolean {
        return when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) -> {
                true
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                false
            }
        }
    }

    private fun registerPermissionCamera() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    Toast.makeText(this, "Разрешение для камеры получено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Не получено разрешение для камеры", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    //Присвоение имени файла фотографии
    private fun getImageFileName(): String {
        if (imageFile?.exists() == true) {
            counter++
        } else {
            counter = 1
        }

        val _imageFileName =
            binding.tvKoordName.text.toString() + " " +
                    binding.etOperationalnumberKIP.editText?.text.toString() + " " +
                    binding.etOperationalKM.editText?.text.toString() + " " +
                    binding.tvLat.text.toString() + " " +
                    binding.tvLon.text.toString() + " " +
                    "($counter)" +
                    ".jpg"

        return _imageFileName
    }

    //Запись фото в файл
    fun savePhotoToStorage(imageUri: Uri, imageFileName: String): Boolean {
        val directoryName = applicationContext?.getString(R.string.app_name)
        val parentDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val storageDir = File(parentDirectory, directoryName)

        if (!storageDir.exists()) storageDir.mkdirs()

        imageFile = File(storageDir, imageFileName)

        val photoBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

        return try {
            val fos = FileOutputStream(imageFile)
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private val fotoResul =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                imageFileName = getImageFileName()
                savePhotoToStorage(imageUri!!, imageFileName)

                tempImageFile?.delete()
                Toast.makeText(
                    this,
                    "Фото сохранено в папку \n Pictures/ЭХЗ трекер",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                tempImageFile?.delete()
//              Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
            }
        }

    private fun createTempImageFile(): File? {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//        Toast.makeText(this, "${Environment.DIRECTORY_PICTURES} ${timeStamp}", Toast.LENGTH_SHORT).show()
        tempImageFile = File.createTempFile(
            timeStamp,
            ".jpg",
            storageDir
        )
        return tempImageFile
    }
}