package com.example.activitylifetime.demoActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.activitylifetime.R
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        val imageId = intent.getIntExtra("ImageId", R.drawable.ic1)
        val text = intent.getStringExtra("Text")
        demoImageView.setImageResource(imageId)
        demoTextView.text = text
    }
}