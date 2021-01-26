package com.example.activitylifetime.lesson8

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.activitylifetime.R
import kotlinx.android.synthetic.main.activity_test_service.*


class TestServiceActivity : AppCompatActivity() {
    private val UPDATE = 1
    private val tag: String = "TestService"
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == UPDATE) {
                handlerTextView.text = java.lang.String.valueOf(msg.obj)
                super.handleMessage(msg)
            }
        }
    }

    private class MyThreadOne : Thread() {
        private var ticket: Int = 5

        override fun run() {
            while (ticket > 0) {
                ticket--;
                Log.i("TestService", name + " 卖掉了1张票, 剩余" + ticket + "张票")
                try {
                    sleep(1000)
                } catch (e: InternalError) {
                    e.printStackTrace()
                }
            }
        }
    }

    private class MyThreadTwo : Thread() {
        private var ticket: Int = 5

        override fun run() {
            while (ticket > 0) {
                ticket--;
                Log.i("TestService", name + " 卖掉了1张票, 剩余" + ticket + "张票")
                try {
                    sleep(1000)
                } catch (e: InternalError) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_service)

        threadButton.setOnClickListener {
            val threadOne = MyThreadOne()
            val threadTwo = MyThreadTwo()
            threadOne.name = "Thread 1 "
            threadTwo.name = "Thread 2 "
            threadOne.start()
            threadTwo.start()
        }

        intentButton.setOnClickListener {
            val intent = Intent(this, TestIntentServiceActivity::class.java)
            startActivity(intent)
        }

        handlerButton.setOnClickListener {
            val thread: Thread = object: Thread() {
                override fun run() {
                    for (i in 0..5) {
                        try {
                            sleep(1000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }

                        val msg = Message()
                        msg.what = UPDATE
                        msg.obj = "更新文本为$i"
                        handler.sendMessage(msg)
                    }
                }
            }
            thread.start()
        }
    }
}