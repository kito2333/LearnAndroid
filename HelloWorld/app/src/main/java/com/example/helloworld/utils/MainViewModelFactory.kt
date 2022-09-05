package com.example.helloworld.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.helloworld.data.MainLiveData
import com.example.helloworld.data.MainViewModel

class MainViewModelFactory(private val count: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainLiveData(count) as T
    }
}
