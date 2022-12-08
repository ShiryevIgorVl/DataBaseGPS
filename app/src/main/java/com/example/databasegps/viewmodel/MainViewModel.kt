package com.example.databasegps.viewmodel

import androidx.lifecycle.*
import com.example.databasegps.database.MainDataBase

import com.example.databasegps.entities.Koordinate
import com.example.databasegps.entities.ParselKoord
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

//@Suppress("UNREACHABLE_CODE", "UNCHECKED_CAST")
class MainViewModel(dataBase: MainDataBase) : ViewModel() {
    val dao = dataBase.getDao()
    val allKoord: LiveData<List<Koordinate>> = dao.getAllKoordinate().asLiveData()

    fun insertKoord(koordinate: Koordinate) = viewModelScope.launch {
        dao.insertKoordinate(koordinate)
    }

    fun deleteKoord(id: Int) = viewModelScope.launch {
        dao.deleteKoordinate(id)
    }
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