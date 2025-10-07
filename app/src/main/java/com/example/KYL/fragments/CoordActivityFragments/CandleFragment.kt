package com.example.KYL.fragments.CoordActivityFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.KYL.R
import com.example.KYL.databinding.FragmentCandleBinding
import com.example.KYL.viewmodel.CandleFragmentViewModel

//Свеча
class CandleFragment : Fragment() {
    private var _binding: FragmentCandleBinding? = null
    private val binding get() = _binding!!

    private val candleFragmentViewModel: CandleFragmentViewModel by activityViewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCandleBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CandleFragment()
    }
}
