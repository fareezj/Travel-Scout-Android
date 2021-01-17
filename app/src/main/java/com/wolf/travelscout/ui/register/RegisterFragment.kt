package com.wolf.travelscout.ui.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.wolf.travelscout.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var navController: NavController

    private var subscription = CompositeDisposable()
    private var username = ""
    private var password = ""
    private var firstName = ""
    private var lastName = ""
    private var email = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        navController = Navigation.findNavController(view)



        btn_register.setOnClickListener {

            username = et_new_username.text.toString()
            password = et_password.text.toString()
            firstName = et_firstName.text.toString()
            lastName = et_lastName.text.toString()
            email = et_email.text.toString()

            registerNewUser(username, password, firstName, lastName, email)
        }

        btn_login_next.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }
        
    }


    private fun registerNewUser(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String){

        val subscribe = viewModel.handleRegisterNewUser(
            username = username,
            password = password,
            firstName = firstName,
            lastName = lastName,
            email = email
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i("REGISTER", "SUCCESS !")
            },{ err -> var msg = err.localizedMessage
                Log.i("DATA", msg.toString())
            })
        subscription.add(subscribe)
    }

}