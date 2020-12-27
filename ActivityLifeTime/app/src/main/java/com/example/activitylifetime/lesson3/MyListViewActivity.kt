package com.example.activitylifetime.lesson3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.activitylifetime.R
import com.example.activitylifetime.adapter.MyListAdapter
import com.example.activitylifetime.data.TestData
import com.example.activitylifetime.demoActivity.DemoActivity
import kotlinx.android.synthetic.main.activity_my_list_view.*

class MyListViewActivity : AppCompatActivity() {
    private val tag = "LayoutActivity"
    private val data = ArrayList<TestData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_list_view)
        initData()
        val adapter = MyListAdapter(
            this,
            R.layout.my_list_view_adapter, data
        )
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val viewHolder = data[position]
            Log.i(tag, " click position $position")
            val intent = Intent(this, DemoActivity::class.java)
            intent.putExtra("ImageId", viewHolder.imageId)
            intent.putExtra("Text", viewHolder.name)
            startActivity(intent)
        }
    }

    private fun initData() {
        repeat(2) {
            data.add(
                TestData(
                    "One",
                    R.drawable.ic1
                )
            )
            data.add(
                TestData(
                    "Two",
                    R.drawable.ic2
                )
            )
            data.add(
                TestData(
                    "Three",
                    R.drawable.ic3
                )
            )
            data.add(
                TestData(
                    "Four",
                    R.drawable.ic4
                )
            )
            data.add(
                TestData(
                    "Five",
                    R.drawable.ic5
                )
            )
            data.add(
                TestData(
                    "Six",
                    R.drawable.ic6
                )
            )
            data.add(
                TestData(
                    "Seven",
                    R.drawable.ic7
                )
            )
            data.add(
                TestData(
                    "Eight",
                    R.drawable.ic8
                )
            )
            data.add(
                TestData(
                    "Nine",
                    R.drawable.ic9
                )
            )
            data.add(
                TestData(
                    "Ten",
                    R.drawable.ic10
                )
            )
        }
    }
}