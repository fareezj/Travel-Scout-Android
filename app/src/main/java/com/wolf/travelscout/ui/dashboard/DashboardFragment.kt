package com.wolf.travelscout.ui.dashboard

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
import com.wolf.travelscout.data.model.UserModel
import com.wolf.travelscout.databinding.FragmentDashboardBinding
import com.wolf.travelscout.util.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navController: NavController
    private var subscription = CompositeDisposable()
    private var friendListTrip = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        navController = Navigation.findNavController(view)

        tv_username.text = SharedPreferencesUtil.username
        handlePrivatePage()

        ib_add_trip.setOnClickListener {
            navController.navigate(R.id.action_dashboardFragment_to_addTripFragment)
        }

        binding.ibUpcomingTrips.setOnClickListener {
            handleGetUserTripList()
        }

    }

    private fun handlePrivatePage(){
        val subscribe = viewModel.handlePrivatePage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i("HELLO", "SUCCESS ! ${it[0]}")
                for(i in it){
                    tv_greeting_message.text = i.message.toString()
                }
            },{ err -> var msg = err.localizedMessage
                Log.i("DATA", err.toString())
            })
        subscription.add(subscribe)
    }

    private fun handleGetUserTripList() {
        val subscribe = viewModel.handleUserTripList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                           for(i in it){
                               Log.i("RESULTS", i.toString())
                               friendListTrip = i.friendList
                           }

                    val decodedJson = Json.decodeFromString<List<UserModel.User>>(friendListTrip)
                    Log.i("DECODED DATA", decodedJson[2].username)


                }, { err -> var msg = err.localizedMessage
                    Log.i("ERROR", msg.toString())
                })
        subscription.add(subscribe)
    }
}