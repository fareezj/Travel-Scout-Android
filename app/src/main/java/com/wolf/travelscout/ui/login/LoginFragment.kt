package com.wolf.travelscout.ui.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.LoginModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private var subscription = CompositeDisposable()
    private var username = ""
    private var password = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        btn_login.setOnClickListener {
            username =   et_username_login.text.toString()
            password =   et_password_login.text.toString()
            handleUserLogin(username, password)
        }
    }

    private fun handleUserLogin(username: String, password: String){
        val subscribe = viewModel.handleUserLogin(
            LoginModel.LoginData(
                username = username,
                password = password
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i("LOGIN", "SUCCESS ! ${it.toString()}")
            },{ err -> var msg = err.localizedMessage
                Log.i("DATA", err.toString())
            })
        subscription.add(subscribe)

    }

}