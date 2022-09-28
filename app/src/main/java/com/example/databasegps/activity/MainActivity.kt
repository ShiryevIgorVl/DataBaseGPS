package com.example.databasegps.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.databasegps.R
import com.example.databasegps.databinding.ActivityMainBinding
import com.example.databasegps.fragments.FragmentManager
import com.example.databasegps.fragments.KoordFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonNavListener()
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