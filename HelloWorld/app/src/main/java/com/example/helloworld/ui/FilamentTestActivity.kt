package com.example.helloworld.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.helloworld.FilamentFactory
import com.example.helloworld.loader.FilamentLoader
import com.example.helloworld.FilamentFactory.RenderType.*

class FilamentTestActivity : AppCompatActivity() {
    companion object {
        const val TAG = "FilamentTestActivity"
        const val BUNDLE_RENDER_TYPE = "renderType"
    }

    private lateinit var loader: FilamentLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val renderType = intent.getStringExtra(BUNDLE_RENDER_TYPE) ?: ""

        loader = FilamentFactory.createFilamentLoader(
            FilamentFactory.RenderType.fromString(renderType)
        )

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