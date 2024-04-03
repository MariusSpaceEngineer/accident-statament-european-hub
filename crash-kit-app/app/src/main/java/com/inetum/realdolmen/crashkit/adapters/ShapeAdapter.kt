package com.inetum.realdolmen.crashkit.adapters

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.inetum.realdolmen.crashkit.R

class ShapesAdapter(private val onShapeClicked: (Int) -> Unit) :
    RecyclerView.Adapter<ShapesAdapter.ViewHolder>() {
    private val shapeResIds = intArrayOf(
        R.drawable.personal_car,
        R.drawable.motorcycle,
        R.drawable.truck,
        R.drawable.four_road_junction,
        R.drawable.roundabout,
        R.drawable.street,
        // Add more shapes here...
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(shapeResIds[position])
        holder.imageView.setOnClickListener { onShapeClicked(shapeResIds[position]) }
    }

    override fun getItemCount() = shapeResIds.size

    class ViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
