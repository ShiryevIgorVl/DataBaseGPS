package com.example.databasegps.activity

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.databasegps.R
import com.example.databasegps.databinding.ActivityKoodBinding
import com.example.databasegps.entities.Koordinate
import com.example.databasegps.fragments.KoordFragment
import java.util.*

class KoordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBarSetting()
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

    // Подключение стрелки назад в акшен баре, id этой стрелки home см. функцию onOptionsItemSelected
    private fun actionBarSetting() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //Передаем данные в KoordFragment из окошек заполнения
    private fun setMainResult() {
        val i = Intent().apply {
            putExtra(KoordFragment.KOORD_KEY, onCreateKoordinate())

        }
        setResult(RESULT_OK, i)
    }

    // Функция заполнения класса Koordinate()
    private fun onCreateKoordinate(): Koordinate{
        return Koordinate(
            null,
            "KIP",
            "22.22222",
            "33.33333",
            "4",
            "5",
            "2.2",
            "мама где моя панама"
        )
    }

    // Функция получения текущего времени
    private fun getTime(): String{
        val formatter = SimpleDateFormat("hh:mm:ss dd.mm.yyyy", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }
}