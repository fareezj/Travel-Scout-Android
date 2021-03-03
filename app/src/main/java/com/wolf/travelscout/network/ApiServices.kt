package com.wolf.travelscout.network

import com.google.gson.GsonBuilder
import com.wolf.travelscout.data.model.LoginModel
import com.wolf.travelscout.data.model.LoginResponse
import com.wolf.travelscout.data.model.UserModel
import com.wolf.travelscout.data.model.dashboard.DashboardRes
import com.wolf.travelscout.data.model.trip.TripModel
import com.wolf.travelscout.util.HeaderInterceptor
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiServices {

    @POST("/register")
    fun registerUser(@Body userData: UserModel.User): Observable<UserModel.User>

    @Headers("Content-Type: application/json")
    @POST("/authenticate")
    fun loginUser(@Body loginData: LoginModel.LoginData): Observable<LoginResponse>

    @GET("/hello")
    fun privatePage(): Observable<List<DashboardRes.DashboardData>>

    @GET("/searchfriend/{username}")
    fun searchFriend(@Path("username") username: String): Observable<List<UserModel.User>>

    @GET("/getCurrentUser/{username}")
    fun getUser(@Path("username") username: String): Observable<UserModel.User>

    @PUT("/addTrip")
    fun addTrip(@Body tripData: TripModel.Trip): Observable<TripModel.Trip>

    @PUT("/updateTrip/{userId}/{upcomingTrip}")
    fun updateUpcomingTrips (@Path("userId") userId: Int, @Path("upcomingTrip") tripId: String)
        : Observable<DashboardRes.DashboardData>

    @GET("/getTrip")
    fun getTrip(): Observable<List<TripModel.Trip>>

    @DELETE("/deleteTrip/{tripId}")
    fun deleteTripById (@Path("tripId") tripId: Int): Observable<DashboardRes.DashboardData>

    @PUT("/updateTrip/{tripId}")
    fun updateTripById(@Path("tripId") tripId: Int, @Body tripData: TripModel.Trip): Observable<TripModel.Trip>

    companion object {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpPrivate = OkHttpClient.Builder().addInterceptor(HeaderInterceptor())
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

        fun getPrivateServices(): ApiServices {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpPrivate.build())
                .build()

            return retrofit.create(ApiServices::class.java)
        }
    }

}