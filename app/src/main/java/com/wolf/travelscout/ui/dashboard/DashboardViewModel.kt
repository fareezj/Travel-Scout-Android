package com.wolf.travelscout.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolf.travelscout.data.model.dashboard.DashboardRes
import com.wolf.travelscout.network.ApiServices
import io.reactivex.Observable

class DashboardViewModel(application: Application): AndroidViewModel(application) {

    private var travelPrivateAPI: ApiServices = ApiServices.getPrivateServices()


    private var _greetingMessage = MutableLiveData<List<DashboardRes.DashboardData>>()
    val greetingMessage: LiveData<List<DashboardRes.DashboardData>> =_greetingMessage


    fun handlePrivatePage(): Observable<List<DashboardRes.DashboardData>> {
        return travelPrivateAPI.privatePage()
    }



}