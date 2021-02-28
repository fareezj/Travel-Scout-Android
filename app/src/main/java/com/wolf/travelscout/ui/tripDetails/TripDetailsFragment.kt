package com.wolf.travelscout.ui.tripDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.UserModel
import com.wolf.travelscout.util.BundleKeys
import com.wolf.travelscout.util.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_trip_details.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class TripDetailsFragment : Fragment() {

    private var tripId: String? = ""
    private var tripHostId: String? = ""
    private var tripName: String? = ""
    private var tripCountry: String? = ""
    private var tripDate: String? = ""
    private var tripType: String? = ""
    private var tripFriends: String? = ""
    private var subscription = CompositeDisposable()
    private lateinit var navController: NavController
    private lateinit var viewModel: TripDetailsViewModel
    private lateinit var adapter : TripFriendListAdapter
    private var decodedFriendList: List<UserModel.User> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(TripDetailsViewModel::class.java)
        navController = Navigation.findNavController(view)
        getDataFromArguments()
        setupView()
    }

    private fun getDataFromArguments() {
        tripId = arguments?.getString(BundleKeys.tripID)
        tripHostId = arguments?.getString(BundleKeys.tripHostID)
        tripName = arguments?.getString(BundleKeys.tripName)
        tripCountry = arguments?.getString(BundleKeys.tripCountry)
        tripDate = arguments?.getString(BundleKeys.tripDate)
        tripType = arguments?.getString(BundleKeys.tripType)
        tripFriends = arguments?.getString(BundleKeys.tripFriends)
    }

    private fun setupView() {
        tv_tripId.text = tripId
        tv_tripHostId.text = tripHostId
        tv_tripName.text = tripName
        tv_tripCountry.text = tripCountry
        tv_tripDate.text = tripDate
        tv_tripType.text = tripType

        decodeFriendList(tripFriends!!)

        btn_trip_delete.setOnClickListener {
            handleDeleteTrip(tripId!!.toInt())
        }
    }

    private fun decodeFriendList(tripFriends: String) {

        decodedFriendList = Json.decodeFromString(tripFriends)
        setupAdapter(decodedFriendList)
    }

    private fun setupAdapter(friendList: List<UserModel.User>){

        rv_trip_friend_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_trip_friend_list.setHasFixedSize(true)
        adapter = TripFriendListAdapter(requireContext(), friendList)
        rv_trip_friend_list.adapter = adapter
    }

    private fun handleDeleteTrip(tripId: Int){
        val subscribe = viewModel.handleDeleteTrip(tripId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.i("SERVER RESPONSE: ", it.message.toString())
                    val snackbar = Snackbar.make(requireView(), "Trip Deleted!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                    navController.navigate(R.id.action_tripDetailsFragment_to_dashboardFragment)
                }, {err -> var msg = err.localizedMessage
                    Log.i("ERROR TRIP DELETE", msg.toString())
                })
        subscription.add(subscribe)
    }
}