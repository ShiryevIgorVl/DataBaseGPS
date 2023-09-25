package com.example.KYL.fragments

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    abstract fun onClickNew()
    abstract suspend fun createExcelTable()
    abstract fun deleteTable()
    abstract fun confirmationAction()
    abstract fun onActionImport()
    abstract fun deleteButton(id: Int)
    abstract fun onClickDelItem(id: Int)


}