package com.wolf.travelscout.data.model

import com.google.gson.annotations.SerializedName

class LoginModel {

    data class LoginData(
        @SerializedName("username")
        var username: String = "",

        @SerializedName("password")
        var password: String = ""
    )
}