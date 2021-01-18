package com.wolf.travelscout.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.wolf.travelscout.data.model.LoginModel
import com.wolf.travelscout.data.model.LoginResponse
import com.wolf.travelscout.network.ApiServices
import io.reactivex.Observable

class LoginViewModel(application: Application): AndroidViewModel(application) {

    private var travelAPI: ApiServices = ApiServices.getServices()

    fun handleUserLogin(loginData: LoginModel.LoginData): Observable<LoginResponse>{
        return travelAPI.loginUser(loginData)
    }


}