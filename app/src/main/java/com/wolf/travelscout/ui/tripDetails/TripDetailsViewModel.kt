package com.wolf.travelscout.ui.tripDetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.wolf.travelscout.data.model.dashboard.DashboardRes
import com.wolf.travelscout.data.model.trip.TripModel
import com.wolf.travelscout.network.ApiServices
import io.reactivex.Observable

class TripDetailsViewModel(application: Application): AndroidViewModel(application) {

    private val travelPrivateAPI: ApiServices = ApiServices.getPrivateServices()

    fun handleDeleteTrip(tripId: Int): Observable<DashboardRes.DashboardData> {
        return travelPrivateAPI.deleteTripById(tripId)
    }
}