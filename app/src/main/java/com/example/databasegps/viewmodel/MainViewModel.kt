package com.example.databasegps.viewmodel

import androidx.lifecycle.*
import com.example.databasegps.database.MainDataBase

import com.example.databasegps.entities.Koordinate
import kotlinx.coroutines.launch

//@Suppress("UNREACHABLE_CODE", "UNCHECKED_CAST")
class MainViewModel(dataBase: MainDataBase) : ViewModel() {
    val dao = dataBase.getDao()
    val allKoord: LiveData<List<Koordinate>> = dao.getAllKoordinate().asLiveData()

    fun insertKoord(koordinate: Koordinate) = viewModelScope.launch {
        dao.insertKoordinate(koordinate)
    }

    //В соответствии с рекомендациями Google Android
    @Suppress("UNREACHABLE_CODE", "UNCHECKED_CAST")
    class MainViewModelFactory(val dataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return super.create(modelClass)
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
              @Suppress ("UNCHECKED_CAST")
                return MainViewModel(dataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}