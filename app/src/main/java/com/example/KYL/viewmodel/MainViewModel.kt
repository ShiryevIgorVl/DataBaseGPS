package com.example.KYL.viewmodel

import androidx.lifecycle.*
import com.example.KYL.database.MainDataBase

import com.example.KYL.entities.Coordinate
import kotlinx.coroutines.launch


//@Suppress("UNREACHABLE_CODE", "UNCHECKED_CAST")
class MainViewModel(dataBase: MainDataBase) : ViewModel() {
    val dao = dataBase.getDao()
    val allKoord: LiveData<List<Coordinate>> = dao.getAllKoordinate().asLiveData()

    fun insertKoord(koordinate: Coordinate) = viewModelScope.launch {
        dao.insertKoordinate(koordinate)
    }

    fun updateKoord(koordinate: Coordinate) = viewModelScope.launch {
        dao.updateKoordinate(koordinate)
    }

    fun deleteKoord(id: Int) = viewModelScope.launch {
        dao.deleteKoordinate(id)
    }

    fun deleteTable() = viewModelScope.launch {
        dao.deleteAllTable()
    }

    fun getCoordinatList() = dao.getAllKoordinateList()

    fun getLastCoordinate() = dao.getLastCoordinate()







    @Suppress("UNCHECKED_CAST")
    //В соответствии с рекомендациями Google Android
    class MainViewModelFactory(val dataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

                return MainViewModel(dataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}