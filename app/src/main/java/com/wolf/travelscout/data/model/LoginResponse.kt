package com.wolf.travelscout.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse (

        @SerializedName("token")
        val token: String
)


//{
//    "access_token":"your.jwt.here",
//    "token_type":"JWT",
//    "expires_in":3600,
//    "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA"
//}