package com.example.helloworld.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.helloworld.data.Fruit

object Repository {

    fun getFruit(fruitId: String): LiveData<Fruit> {
        val liveData = MutableLiveData<Fruit>()
        liveData.value = Fruit(fruitId, 0)
        return liveData
    }
}