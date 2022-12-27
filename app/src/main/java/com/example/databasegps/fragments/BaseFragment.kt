package com.example.databasegps.fragments

import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {
    abstract fun onClickNew()
    abstract fun createExcelTable()

}