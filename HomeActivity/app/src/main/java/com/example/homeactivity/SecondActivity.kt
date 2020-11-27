package com.example.homeactivity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("SecondActivity", "onCreate")
        setContentView(R.layout.activity_second)
        button2.setOnClickListener {
            val backIntent = Intent()
            Log.i("SecondActivity", "click Button 2")
            backIntent.putExtra("returnData", "Hello World!")
            setResult(Activity.RESULT_OK, backIntent)
            finish()
        }
    }
}