package com.example.helloworld.web

import android.util.Log
import org.json.JSONArray

class JsonDataJSONObjectParser : DataParser {
    companion object {
        const val TAG = "JSONObjectParser"
    }

    override fun parse(data: String) {
        try {
            val jsonArray = JSONArray(data)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                Log.d(TAG, "id is ${jsonObject.getString("id")}")
                Log.d(TAG, "name is ${jsonObject.getString("name")}")
                Log.d(TAG, "version is ${jsonObject.getString("version")}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}