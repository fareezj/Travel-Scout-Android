package com.wolf.travelscout.data.model

import com.google.gson.annotations.SerializedName

class UserModel {

    data class User(

        @SerializedName("user_id")
        var userID: Int = 0,

        @SerializedName("username")
        var username: String = "",

        @SerializedName("password")
        var password: String = "",

        @SerializedName("firstName")
        var firstName: String = "",

        @SerializedName("phone")
        var phone: String = "",

        @SerializedName("email")
        var email: String = "",

        @SerializedName("profileImage")
        var profileImage: String = ""
    )

}