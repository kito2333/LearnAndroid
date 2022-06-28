package com.example.helloworld.web

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception

class JsonDataGsonParser : DataParser {
    companion object {
        const val TAG = "JsonDataGsonParser"
    }

    override fun parse(data: String) {
        try {
            val gson = Gson()
            val typeOf = object : TypeToken<List<App>>() {}.type
            val appList = gson.fromJson<List<App>>(data, typeOf)
            for (app in appList) {
                Log.d(TAG, "id is ${app.id}, name is ${app.name}, version is ${app.version}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class App(val id: String, val name: String, val version: String)