package com.example.activitylifetime.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.activitylifetime.R
import com.example.activitylifetime.data.TestData

class MyListAdapter(activity: Activity, private val layoutId: Int, data: List<TestData>) :
    ArrayAdapter<TestData>(activity, layoutId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(layoutId, parent, false)
        val imageView: ImageView = view.findViewById(R.id.testImageView)
        val textView: TextView = view.findViewById(R.id.testTextView)
        val viewHolder = getItem(position)
        viewHolder?.let {
            imageView.setImageResource(it.imageId)
            textView.text = it.name
        }
        return view
    }
}