package com.wolf.travelscout.network

import com.wolf.travelscout.data.model.LoginModel
import com.wolf.travelscout.data.model.UserModel
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiServices {

    @POST("/register")
    fun registerUser(@Body userData: UserModel.User): Observable<UserModel.User>

    @Headers("Content-Type: application/json")
    @POST("/authenticate")
    fun loginUser(@Body loginData: LoginModel.LoginData): Observable<LoginModel.LoginData>


    //@Field("username") username: String, @Field("password") password: String

    companion object {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttp = OkHttpClient.Builder().addInterceptor(logger)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder().build()
                chain.proceed(newRequest)
            }


        fun getServices(): ApiServices {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttp.build())
                .build()

            return retrofit.create(ApiServices::class.java)
        }
    }

}