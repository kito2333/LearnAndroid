package com.example.activitylifetime.lauchmode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.activitylifetime.R
import kotlinx.android.synthetic.main.activity_3.*

class Activity3 : AppCompatActivity() {
    private val tag: String = "Activity3"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "$this onCreate, task id is $taskId, process id is ${android.os.Process.myPid()}")
        setContentView(R.layout.activity_3)
        button3_1.setOnClickListener {
            val intent = Intent(this, Activity1::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        Log.i(tag, "$this onDestroy")
        super.onDestroy()
    }
}