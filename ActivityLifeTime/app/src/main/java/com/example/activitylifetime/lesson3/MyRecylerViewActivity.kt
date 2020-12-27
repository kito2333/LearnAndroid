package com.example.activitylifetime.lesson3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.activitylifetime.R
import com.example.activitylifetime.adapter.MyRecyclerViewAdapter
import com.example.activitylifetime.data.TestData
import kotlinx.android.synthetic.main.activity_my_recyler_view.*

class MyRecylerViewActivity : AppCompatActivity() {

    private val dataList = ArrayList<TestData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_recyler_view)
        initData()
        val layoutManager = GridLayoutManager( this, 4)
        val adapter = MyRecyclerViewAdapter(dataList)
        myRecyclerView.layoutManager = layoutManager
        myRecyclerView.adapter = adapter
    }

    private fun initData() {
        repeat(2) {
            dataList.add(
                TestData(
                    getRandomLengthString("One"),
                    R.drawable.ic1
                )
            )
            dataList.add(
                TestData(
                    getRandomLengthString("Two"),
                    R.drawable.ic2
                )
            )
            dataList.add(
                TestData(
                    getRandomLengthString("Three"),
                    R.drawable.ic3
                )
            )
            dataList.add(
                TestData(
                    getRandomLengthString("Four"),
                    R.drawable.ic4
                )
            )
            dataList.add(
                TestData(
                    getRandomLengthString("Five"),
                    R.drawable.ic5
                )
            )
            dataList.add(
                TestData(
                    getRandomLengthString("Six"),
                    R.drawable.ic6
                )
            )
            dataList.add(
                TestData(
                    getRandomLengthString("Seven"),
                    R.drawable.ic7
                )
            )
            dataList.add(
                TestData(
                    getRandomLengthString("Eight"),
                    R.drawable.ic8
                )
            )
            dataList.add(
                TestData(
                    getRandomLengthString("Nine"),
                    R.drawable.ic9
                )
            )
            dataList.add(
                TestData(
                    getRandomLengthString("Ten"),
                    R.drawable.ic10
                )
            )

        }
    }

    private fun getRandomLengthString(str: String): String {
        val n = (1..40).random()
        val builder = StringBuilder()
        repeat(n) {
            builder.append(str)
        }
        return builder.toString()
    }
}