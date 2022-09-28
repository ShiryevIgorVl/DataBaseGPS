package com.example.databasegps.fragments

import androidx.appcompat.app.AppCompatActivity
import com.example.databasegps.R
import com.example.databasegps.activity.MainActivity

object FragmentManager {
    var currentFragment: BaseFragment? = null

    fun setFragment(newFragment: BaseFragment, activity: AppCompatActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.placeHolder, newFragment)
        transaction.commit()
        currentFragment = newFragment
    }
}