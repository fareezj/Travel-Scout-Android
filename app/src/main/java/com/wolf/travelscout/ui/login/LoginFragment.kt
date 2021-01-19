package com.wolf.travelscout.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.LoginModel
import com.wolf.travelscout.util.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var navController: NavController
    private var subscription = CompositeDisposable()
    private var username = ""
    private var password = ""
    private var accessToken = ""


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
        navController = Navigation.findNavController(view)
        setupComponent()


        btn_login.setOnClickListener {
            username =   et_username_login.text.toString()
            password =   et_password_login.text.toString()
            SharedPreferencesUtil.username = username
            handleUserLogin(username, password)
        }

        btn_hello.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_dashboardFragment)
        }
    }

    private fun setupComponent(){
        btn_login.isEnabled = false

        et_username_login.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {validateEditTextLength()}
        })

        et_password_login.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {validateEditTextLength()}
        })

        et_username_login.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                if(et_username_login.text.isNullOrEmpty()){
                    et_username_login.error = "Please enter username"
                }
            }
        }

        et_password_login.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                if(et_password_login.text.isNullOrEmpty()){
                    et_password_login.error = "Please enter password"
                }
            }
        }

    }

    private fun validateEditTextLength(){
        btn_login.isEnabled =
                        et_username_login.text!!.isNotEmpty() &&
                        et_password_login.text!!.isNotEmpty()
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
                accessToken = it.token.toString()
                Log.i("LOGIN", "SUCCESS ! $accessToken")
                SharedPreferencesUtil.accessToken = accessToken
            },{ err -> var msg = err.localizedMessage
                Log.i("DATA", err.toString())
            })
        subscription.add(subscribe)

    }

}