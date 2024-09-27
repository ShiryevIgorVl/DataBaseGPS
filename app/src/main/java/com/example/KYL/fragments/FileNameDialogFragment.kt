package com.example.KYL.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.KYL.R
import com.example.KYL.databinding.DialogFileNameBinding

class FileNameDialogFragment: DialogFragment() {
    private var _binding: DialogFileNameBinding? = null
    private val binding get() = _binding!!

    private lateinit var fileNameEditText: EditText

    var onFileNameEntered: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFileNameBinding.inflate(inflater, container, false)
        val root: View = binding.root
        fileNameEditText = binding.fileNameEditText
        binding.saveButton.setOnClickListener {
            val fileName = fileNameEditText.text.toString()
            onFileNameEntered?.invoke(fileName)
            dismiss()
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null){
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}