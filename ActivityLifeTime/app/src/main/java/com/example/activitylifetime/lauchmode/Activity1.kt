package com.example.activitylifetime.lauchmode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.activitylifetime.R
import kotlinx.android.synthetic.main.activity_1.*

class Activity1 : AppCompatActivity() {
    private val tag: String = "Activity1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "$this onCreate, task id is $taskId")
        setContentView(R.layout.activity_1)
        button1_2.setOnClickListener {
            val intent = Intent(this, Activity2::class.java)
            startActivity(intent)
        }
        button1_3.setOnClickListener {
            val intent = Intent(this, Activity3::class.java)
            startActivity(intent)
        }
        button1_1.setOnClickListener(object: MyOnClickListener() {
            override fun onClick(v: View?) {
                super.onClick(v)
                val intent = Intent(this@Activity1, Activity1::class.java)
                startActivity(intent)
            }
        })
    }

    open class MyOnClickListener: View.OnClickListener {
        override fun onClick(v: View?) {
            Log.i("tag", " jiayou!")
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

    override fun onNewIntent(intent: Intent?) {
        Log.i(tag, "$this onNewIntent")
        super.onNewIntent(intent)
    }
}