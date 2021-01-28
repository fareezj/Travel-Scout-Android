package com.wolf.travelscout.data.model.trip

import com.google.gson.annotations.SerializedName

class TripModel {

    data class Trip(
            @SerializedName("tripId")
            var tripId: Int = 0,

            @SerializedName("hostId")
            var hostId: Int = 0,

            @SerializedName("country")
            var country: String = "",

            @SerializedName("tripName")
            var tripName: String = "",

            @SerializedName("tripDate")
            var tripDate: String = "",

            @SerializedName("tripType")
            var tripType: String = "",

            @SerializedName("friendList")
            var friendList: String = "",
    )
}