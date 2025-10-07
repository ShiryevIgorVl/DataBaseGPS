package com.example.KYL.fragments.CoordActivityFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.KYL.R
import com.example.KYL.databinding.FragmentInsulationDamageBinding
import com.example.KYL.viewmodel.IndicatorFragmentViewModel
import com.example.KYL.viewmodel.IsulationDamageViewModel

//Повреждение изоляции
class InsulationDamageFragment : Fragment() {
    private var _binding: FragmentInsulationDamageBinding?= null
    private val binding get() = _binding!!

    private val insulationDamageViewModel: IsulationDamageViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInsulationDamageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() = InsulationDamageFragment()

    }
}