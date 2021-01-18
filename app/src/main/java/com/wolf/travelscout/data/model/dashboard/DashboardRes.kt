package com.wolf.travelscout.data.model.dashboard

import com.google.gson.annotations.SerializedName

class DashboardRes {

    data class DashboardData(
            @SerializedName("message")
            var message: String = "",

    )
}