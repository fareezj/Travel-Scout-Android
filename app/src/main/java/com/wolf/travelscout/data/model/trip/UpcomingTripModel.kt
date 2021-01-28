package com.wolf.travelscout.data.model.trip

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

class UpcomingTripModel {

    @Serializable
    data class TripID(

        @SerializedName("tripId")
        var tripId: Int = 0,
    )
}