package com.example.databasegps.fragments

import androidx.fragment.app.Fragment
import com.example.databasegps.entities.Koordinate

abstract class BaseFragment: Fragment() {
    abstract fun onClickNew()
    abstract fun createExcelTable()

}