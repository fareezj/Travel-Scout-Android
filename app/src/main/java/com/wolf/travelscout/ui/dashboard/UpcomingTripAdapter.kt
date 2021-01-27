package com.wolf.travelscout.ui.dashboard

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.trip.TripModel
import kotlinx.android.synthetic.main.upcoming_trip_item.view.*

class UpcomingTripAdapter (val context: Context, var items: ArrayList<TripModel.Trip>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UpcomingTripViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.upcoming_trip_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is UpcomingTripViewHolder){
            holder.itemView.tv_upcoming_trip_title.text = items[position].tripName

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class UpcomingTripViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }


}