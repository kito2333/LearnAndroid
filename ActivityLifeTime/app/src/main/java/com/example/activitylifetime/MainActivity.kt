package com.example.activitylifetime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.activitylifetime.lauchmode.Activity1
import com.example.activitylifetime.lesson3.MyListViewActivity
import com.example.activitylifetime.lesson3.MyRecylerViewActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val tag: String = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")
        setContentView(R.layout.activity_main)
        button1.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener {
            val intent = Intent(this, Activity1::class.java)
            startActivity(intent)
        }
        button4.setOnClickListener{
            val intent = Intent(this, TestJavaActivity::class.java)
            startActivity(intent)
        }
        button5.setOnClickListener {
            val intent = Intent(this, MyListViewActivity::class.java)
            startActivity(intent)
        }
        button6.setOnClickListener {
            val intent = Intent(this, MyRecylerViewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(tag, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(tag, "onResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(tag, "onRestart")
    }

    override fun onStop() {
        super.onStop()
        Log.i(tag, "onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.i(tag, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "onDestroy")
    }
}