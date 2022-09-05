package com.example.helloworld.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.helloworld.utils.Repository

class MainLiveData(countReserved: Int) : ViewModel() {
    val counter: LiveData<Int>
        get() = _counter

    private val _counter = MutableLiveData<Int>()

    init {
        _counter.value = countReserved
    }

    fun plusOne() {
        val count = _counter.value ?: 0
        _counter.value = count + 1
    }

    fun clear() {
        _counter.value = 0
    }

    private val fruitLiveData = MutableLiveData<Fruit>()

    val fruitName: LiveData<String> = Transformations.map(fruitLiveData) {
        "fruit ${it.name}"
    }

    fun getFruitErr(id: String): LiveData<Fruit> {
        return Repository.getFruit(id)
    }

    private val fruitIdLiveData = MutableLiveData<String>()

    val fruit: LiveData<Fruit> = Transformations.switchMap(fruitIdLiveData) {
        Repository.getFruit(it)
    }

    fun getFruit(id: String) {
        fruitIdLiveData.value = id
    }
}