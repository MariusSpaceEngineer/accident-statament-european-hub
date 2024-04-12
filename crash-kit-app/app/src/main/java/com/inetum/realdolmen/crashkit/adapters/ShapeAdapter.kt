package com.inetum.realdolmen.crashkit.adapters

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.inetum.realdolmen.crashkit.R

class ShapesAdapter(private val onShapeClicked: (Int, Int) -> Unit) :
    RecyclerView.Adapter<ShapesAdapter.ViewHolder>() {

    //The list of shapes with a priority number, the higher the number, the higher the priority
    private val shapeResIds = mapOf(
        R.drawable.personal_car_vehicle_a to 1,
        R.drawable.personal_car_vehicle_b to 1,
        R.drawable.motorcycle_vehicle_a to 1,
        R.drawable.motorcycle_vehicle_b to 1,
        R.drawable.truck_vehicle_a to 1,
        R.drawable.truck_vehicle_b to 1,
        R.drawable.four_road_junction to 0,
        R.drawable.t_junction to 0,
        R.drawable.roundabout to 0,
        R.drawable.road_180 to 0,
        R.drawable.road_90 to 0,
        R.drawable.direction_arrow to 2,
        R.drawable.stop_sign to 2,
        R.drawable.yield_sign to 2,
        R.drawable.no_entry_sign to 2,
        R.drawable.traffic_light_red to 2,
        R.drawable.traffic_light_green to 2
    ).toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val screenWidth = determineUserScreenSize(parent)
        val imageViewWidth = screenWidth / 3 // for three images per row
        val imageViewHeight = 600

        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(imageViewWidth, imageViewHeight)
        }
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (resId, priority) = shapeResIds[position]
        holder.imageView.setImageResource(resId)
        holder.imageView.setOnClickListener { onShapeClicked(resId, priority) }
    }

    override fun getItemCount() = shapeResIds.size

    private fun determineUserScreenSize(parent: ViewGroup): Int {
        val displayMetrics = parent.context.resources.displayMetrics
        return displayMetrics.widthPixels
    }

    class ViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
