package com.example.KYL.fragments

import androidx.fragment.app.Fragment
import com.example.KYL.entities.Coordinate

abstract class BaseFragment : Fragment() {
    abstract fun onClickNew()
    abstract suspend fun createExcelTable()
    abstract fun deleteTable()
    abstract fun confirmationAction()
    abstract fun onActionImport()

}