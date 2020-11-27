package com.example.activitylifetime.lauchmode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.activitylifetime.R
import kotlinx.android.synthetic.main.activity_2.*

class Activity2 : AppCompatActivity() {
    private val tag:String = "Activity2"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "$this onCreate, task id is $taskId")
        setContentView(R.layout.activity_2)
        button2_1.setOnClickListener {
            val intent = Intent(this, Activity1::class.java)
            startActivity(intent)
        }
        button2_2.setOnClickListener {
            val intent = Intent(this, Activity3::class.java)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        Log.i(tag, "$this onRestart")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.i(tag, "$this onDestroy")
        super.onDestroy()
    }
}