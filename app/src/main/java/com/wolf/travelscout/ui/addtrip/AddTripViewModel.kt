package com.wolf.travelscout.ui.addtrip

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.wolf.travelscout.data.model.UserModel
import com.wolf.travelscout.network.ApiServices
import io.reactivex.Observable
import java.util.*

class AddTripViewModel(application: Application): AndroidViewModel(application) {

    private var travelAPI: ApiServices = ApiServices.getPrivateServices()

    fun handleSearchFriend(username: String): Observable<List<UserModel.User>>{
        return travelAPI.searchFriend(username)
    }

}