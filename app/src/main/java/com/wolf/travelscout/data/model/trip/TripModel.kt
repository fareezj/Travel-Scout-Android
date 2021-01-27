package com.wolf.travelscout.data.model.trip

import com.google.gson.annotations.SerializedName

class TripModel {

    data class Trip(
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