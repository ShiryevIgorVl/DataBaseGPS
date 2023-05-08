package com.example.KYL.fragments

import androidx.fragment.app.Fragment
import com.example.KYL.entities.Coordinate

abstract class BaseFragment : Fragment() {
    abstract fun onClickNew()
    abstract fun createExcelTable()
    abstract fun deleteTable()
    abstract fun confirmationAction()

}