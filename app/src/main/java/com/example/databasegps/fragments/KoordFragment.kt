package com.example.databasegps.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.databasegps.activity.App
import com.example.databasegps.activity.KoordActivity
import com.example.databasegps.databinding.FragmentKoordBinding
import com.example.databasegps.entities.Koordinate
import com.example.databasegps.recyclerview.KoordAdapter
import com.example.databasegps.viewmodel.MainViewModel


class KoordFragment : BaseFragment(), KoordAdapter.Listener {

    private lateinit var binding: FragmentKoordBinding
    private lateinit var koordResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: KoordAdapter
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }


    //
    override fun onClickNew() {
        koordResultLauncher.launch(Intent(activity, KoordActivity::class.java))
    }

    private fun onKoordResult() {
        koordResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    mainViewModel.insertKoord(it.data?.getSerializableExtra(KOORD_KEY) as Koordinate)
                    Log.d("MyLog", "KoordFragment:: it.resultCode: ${it.resultCode},  Activity.resultCode: ${Activity.RESULT_OK}")
                }else{
                    Log.d("MyLog", "KoordFragment:: it.resultCode: ${it.resultCode},  Activity.resultCode: ${Activity.RESULT_OK}")
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onKoordResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKoordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        observer()
    }

    private fun observer() {
        mainViewModel.allKoord.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun initAdapter() = with(binding) {
        rvKoord.layoutManager = LinearLayoutManager(activity)
        adapter = KoordAdapter(this@KoordFragment)
        rvKoord.adapter = adapter
    }

    override fun onClickDelItem(id: Int) {
        mainViewModel.deleteKoord(id)
    }


    companion object {
        const val KOORD_KEY = "koord_key"

        @JvmStatic
        fun newInstance() = KoordFragment()

    }
}