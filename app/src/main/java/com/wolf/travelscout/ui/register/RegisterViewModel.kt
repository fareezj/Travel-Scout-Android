package com.wolf.travelscout.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.wolf.travelscout.data.model.UserModel
import com.wolf.travelscout.network.ApiServices
import io.reactivex.Observable

class RegisterViewModel(application: Application): AndroidViewModel(application) {

    private var travelAPI: ApiServices = ApiServices.getServices()

    fun handleRegisterNewUser(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String): Observable<UserModel.User>
    {
        return travelAPI.registerUser(
            UserModel.User(
                username = username,
                password = password,
                firstName = firstName,
                lastName = lastName,
                email = email
            )
        )
    }

}