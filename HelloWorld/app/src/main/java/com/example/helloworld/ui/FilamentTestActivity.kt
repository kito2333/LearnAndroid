package com.example.helloworld.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.helloworld.loader.FilamentLoader
import com.example.helloworld.loader.RectFilamentLoader

class FilamentTestActivity : AppCompatActivity() {
    companion object {
        const val TAG = "FilamentTestActivity"
    }

    private val loader: FilamentLoader = RectFilamentLoader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loader.init(this)
        setContentView(loader.surfaceView)
    }

    override fun onResume() {
        super.onResume()
        loader.resume()
    }

    override fun onPause() {
        super.onPause()
        loader.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        loader.destroy()
    }
}