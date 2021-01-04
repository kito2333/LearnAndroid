package com.example.activitylifetime.lesson4

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
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

 
        /**
         * 对user表进行操作
         */
        // 设置URI
        val uriUser = Uri.parse("content://cn.scu.myprovider/user")

        // 插入表中数据

        // 插入表中数据
        val values = ContentValues()
        values.put("_id", 3)
        values.put("name", "Iverson")


        // 获取ContentResolver


        // 获取ContentResolver
        val resolver = contentResolver
        // 通过ContentResolver 根据URI 向ContentProvider中插入数据
        // 通过ContentResolver 根据URI 向ContentProvider中插入数据
        resolver.insert(uriUser, values)

        // 通过ContentResolver 向ContentProvider中查询数据

        // 通过ContentResolver 向ContentProvider中查询数据
        val cursor: Cursor? =
            resolver.query(uriUser, arrayOf("_id", "name"), null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                println(
                    "query book:" + cursor.getInt(0).toString() + " " + cursor.getString(1)
                )
                // 将表中数据全部输出
            }
        }
        cursor?.close()
        // 关闭游标


        /**
         * 对job表进行操作
         */
        // 和上述类似,只是URI需要更改,从而匹配不同的URI CODE,从而找到不同的数据资源
        val uriJob = Uri.parse("content://cn.scu.myprovider/job")

        // 插入表中数据

        // 插入表中数据
        val values2 = ContentValues()
        values2.put("_id", 3)
        values2.put("job", "NBA Player")

        // 获取ContentResolver

        // 获取ContentResolver
        val resolver2 = contentResolver
        // 通过ContentResolver 根据URI 向ContentProvider中插入数据
        // 通过ContentResolver 根据URI 向ContentProvider中插入数据
        resolver2.insert(uriJob, values2)

        // 通过ContentResolver 向ContentProvider中查询数据

        // 通过ContentResolver 向ContentProvider中查询数据
        val cursor2: Cursor? =
            resolver2.query(uriJob, arrayOf("_id", "job"), null, null, null)
        if (cursor2 != null) {
            while (cursor2.moveToNext()) {
                println(
                    "query job:" + cursor2.getInt(0).toString() + " " + cursor2.getString(1)
                )
                // 将表中数据全部输出
            }
        }
        cursor2?.close()
        // 关闭游标
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
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}