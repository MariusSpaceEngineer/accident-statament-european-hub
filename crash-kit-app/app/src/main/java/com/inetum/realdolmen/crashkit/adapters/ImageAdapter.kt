package com.inetum.realdolmen.crashkit.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.inetum.realdolmen.crashkit.R

class ImageAdapter(private val imageSet: MutableList<Bitmap>, private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val removeButton: Button = itemView.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.image_view_pager_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageBitmap(imageSet[position])
        holder.removeButton.setOnClickListener {
            // Remove the image from the list
            imageSet.removeAt(position)
            // Notify the adapter that the item was removed
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, imageSet.size)
        }
    }
}

