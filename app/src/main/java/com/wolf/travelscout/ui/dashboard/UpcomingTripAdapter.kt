package com.wolf.travelscout.ui.dashboard

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.trip.TripModel
import com.wolf.travelscout.data.model.trip.UpcomingTripModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.upcoming_trip_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class UpcomingTripAdapter (val context: Context, var items: ArrayList<TripModel.Trip>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val publishSubject = PublishSubject.create<TripModel.Trip>()
    val observeEvent: Observable<TripModel.Trip> = publishSubject


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UpcomingTripViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.upcoming_trip_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is UpcomingTripViewHolder){
            holder.itemView.tv_upcoming_trip_title.text = items[position].tripName
            holder.itemView.cl_trip_item.setOnClickListener {
                publishSubject.onNext(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class UpcomingTripViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }


}