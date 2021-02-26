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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.UserModel
import com.wolf.travelscout.data.model.trip.TripModel
import com.wolf.travelscout.data.model.trip.UpcomingTripModel
import com.wolf.travelscout.databinding.FragmentDashboardBinding
import com.wolf.travelscout.util.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navController: NavController
    private lateinit var upcomingTripAdapter: UpcomingTripAdapter
    private var subscription = CompositeDisposable()
    private var upcomingTripList: ArrayList<TripModel.Trip> = arrayListOf()
    private var username: String? = ""
    private var totalTrips: Int = 0


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
        username = SharedPreferencesUtil.username
        tv_username.text = username
        handlePrivatePage()
        handleCurrentUserData(username!!)
        handleGetUserTripList()
        binding.tvGreetingMessage.text = totalTrips.toString()
        //handleUpdateFriendsUpcomingTrip(2, "1")
        handleCurrentUserData("Fareez")


        ib_add_trip.setOnClickListener {
            navController.navigate(R.id.action_dashboardFragment_to_addTripFragment)
        }

        binding.ibUpcomingTrips.setOnClickListener {
            setupUpcomingTripAdapter(upcomingTripList)
        }

    }

    private fun setupUpcomingTripAdapter(tripList: ArrayList<TripModel.Trip>){

        rv_upcoming_trip.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_upcoming_trip.setHasFixedSize(true)
        upcomingTripAdapter = UpcomingTripAdapter(requireContext(), tripList)
        rv_upcoming_trip.adapter = upcomingTripAdapter
        upcomingTripAdapter.observeEvent.subscribe {
            findNavController().navigate(
                R.id.action_dashboardFragment_to_tripDetailsFragment
            )
        }
    }

    private fun handleCurrentUserData(username: String) {
        val subscribe = viewModel.handleGetCurrentUser(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    SharedPreferencesUtil.userID = it.userID
                    Log.i("SHARED PREF USER ID", SharedPreferencesUtil.userID.toString())
                    Log.i("TRIP COUNT FOR FAREEZ====", it.upcomingTrip)

                }, {err -> var msg = err.localizedMessage
                    Log.i("ERROR", msg.toString())
                })
        subscription.add(subscribe)
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
                    upcomingTripList.clear()
                    for(i in it){
                        if(SharedPreferencesUtil.userID == i.hostId){
                            upcomingTripList.add(i)
                            Log.i("MATCHED ID", i.hostId.toString())
                        }
                    }

                }, { err -> var msg = err.localizedMessage
                    Log.i("ERROR", msg.toString())
                })
        subscription.add(subscribe)
    }


}