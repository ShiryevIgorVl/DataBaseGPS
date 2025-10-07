package com.example.KYL.fragments

import androidx.appcompat.app.AppCompatActivity
import com.example.KYL.R
import com.example.KYL.fragments.CoordActivityFragments.ButtonPointFragment

object PointFragmentManager {
    var currentPointFragment: PointBaseFragment? = null

fun setPointFragment(newFragment: PointBaseFragment, activity: AppCompatActivity){
    val transaction = activity.supportFragmentManager.beginTransaction()
    transaction.replace(R.id.fragmentButtonHolder, newFragment)
    transaction.commit()

    currentPointFragment = newFragment
    }
}