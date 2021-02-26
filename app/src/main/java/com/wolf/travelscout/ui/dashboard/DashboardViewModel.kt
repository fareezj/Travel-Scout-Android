package com.wolf.travelscout.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolf.travelscout.data.model.UserModel
import com.wolf.travelscout.data.model.dashboard.DashboardRes
import com.wolf.travelscout.data.model.trip.TripModel
import com.wolf.travelscout.network.ApiServices
import io.reactivex.Observable
import retrofit2.Response

class DashboardViewModel(application: Application): AndroidViewModel(application) {

    private var travelPrivateAPI: ApiServices = ApiServices.getPrivateServices()


    private var _greetingMessage = MutableLiveData<List<DashboardRes.DashboardData>>()
    val greetingMessage: LiveData<List<DashboardRes.DashboardData>> =_greetingMessage


    fun handlePrivatePage(): Observable<List<DashboardRes.DashboardData>> {
        return travelPrivateAPI.privatePage()
    }

    fun handleUserTripList(): Observable<List<TripModel.Trip>> {
        return travelPrivateAPI.getTrip()
    }

    fun handleGetCurrentUser(username: String): Observable<UserModel.User> {
        return travelPrivateAPI.getUser(username)
    }




}