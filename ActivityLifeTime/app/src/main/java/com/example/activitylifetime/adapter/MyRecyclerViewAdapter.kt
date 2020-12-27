package com.example.activitylifetime.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.activitylifetime.R
import com.example.activitylifetime.data.TestData
import com.example.activitylifetime.demoActivity.DemoActivity

class MyRecyclerViewAdapter(private val dataList: List<TestData>) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.myRVAImageView)
        val textView: TextView = view.findViewById(R.id.myRVATextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_recycler_view_adapter, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.imageView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val data = dataList[position]
            val intent = Intent(parent.context, DemoActivity::class.java)
            intent.putExtra("ImageId", data.imageId)
            intent.putExtra("Text", data.name)
            parent.context.startActivity(intent)
        }
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val data = dataList[position]
            Toast.makeText(parent.context, "You Click ${data.name}", Toast.LENGTH_LONG).show()
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.imageView.setImageResource(data.imageId)
        holder.textView.text = data.name
    }
}