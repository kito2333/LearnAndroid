package com.example.activitylifetime.lesson4

import android.content.ContentProvider
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.activitylifetime.R
import kotlinx.android.synthetic.main.activity_test_permission.*

class TestPermissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_permission)
        TPAbutton1.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    5
                )
            } else {
                callPhone()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            5 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone()
                } else {
                    Log.i("ZZC", " permission denied")
                }
            }
        }
    }

    private fun callPhone() {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:666666")
            startActivity(intent)
            ContentProvider
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}