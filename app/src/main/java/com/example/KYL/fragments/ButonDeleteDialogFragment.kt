package com.example.KYL.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.KYL.R

class ButonDeleteDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Подтвердите удаление записи")
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setCancelable(true)
                .setPositiveButton("Удалить") { _, _ ->
                    FragmentManager.currentFragment?.deleteButton(id)
                }
                .setNegativeButton(
                    "Отмена"
                ) { _, _ ->
                    return@setNegativeButton
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}