package com.wolf.travelscout.ui.addtrip

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.wolf.travelscout.data.model.UserModel
import com.wolf.travelscout.data.model.dashboard.DashboardRes
import com.wolf.travelscout.data.model.trip.TripModel
import com.wolf.travelscout.network.ApiServices
import io.reactivex.Observable
import java.util.*

class AddTripViewModel(application: Application): AndroidViewModel(application) {

    private var travelAPI: ApiServices = ApiServices.getPrivateServices()
    private var travelPrivateAPI: ApiServices = ApiServices.getPrivateServices()

    fun handleSearchFriend(username: String): Observable<List<UserModel.User>>{
        return travelAPI.searchFriend(username)
    }

    fun handleUserTripList(): Observable<List<TripModel.Trip>> {
        return travelPrivateAPI.getTrip()
    }


    fun handleUpdateFriendsUpcomingTrip(userId: Int, upcomingTrip: String): Observable<DashboardRes.DashboardData>{
        return travelPrivateAPI.updateUpcomingTrips(userId, upcomingTrip)
    }

    fun handleAddTrip(
            hostId: Int,
            country: String,
            tripName: String,
            tripDate: String,
            tripType: String,
            friendList: String): Observable<TripModel.Trip>{
            return travelAPI.addTrip(
                    TripModel.Trip(
                            hostId = hostId,
                            country = country,
                            tripName = tripName,
                            tripDate = tripDate,
                            tripType = tripType,
                            friendList = friendList
                    )
            )
    }

}