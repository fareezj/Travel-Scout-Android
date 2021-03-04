package com.wolf.travelscout.ui.addtrip

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.UserModel
import com.wolf.travelscout.data.model.trip.TripModel
import com.wolf.travelscout.util.BundleKeys
import com.wolf.travelscout.util.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_add_trip.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*


class AddTripFragment : Fragment() {

    private var subscription = CompositeDisposable()
    private lateinit var viewModel: AddTripViewModel
    private lateinit var adapter: SearchFriendResultAdapter
    private lateinit var addedFriendAdapter: FriendAddedAdapter
    private lateinit var navController: NavController
    private var fragmentMode = ""
    private var friendList: ArrayList<UserModel.User> = arrayListOf()
    private var searchName: String = ""
    private var selectedTripFriend: ArrayList<UserModel.User> = arrayListOf()
    private var invitedTrip: ArrayList<Int> = arrayListOf()
    private var tripCountry: String = "Malaysia"
    private var tripName: String = ""
    private var tripDate: String = ""
    private var tripType: String = ""
    private var hostID: Int = 0
    private var tripListData: ArrayList<TripModel.Trip> = arrayListOf()
    private var tripId: ArrayList<Int> = arrayListOf()
    private var getTripId: Int = 0
    private var getFetchedFriends: String = ""
    private var getHostID: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AddTripViewModel::class.java)
        navController = Navigation.findNavController(view)
        setupComponent()
        getModeFromArgument()
        if(fragmentMode == "EditTripMode"){
            setupEditMode();
        }

        btn_deserialize.setOnClickListener {

            val myArrString = Json.encodeToString(selectedTripFriend)
            val deserializer = Json.decodeFromString<List<UserModel.User>>(myArrString)
        }

    }

    private fun getModeFromArgument(){
        fragmentMode = arguments?.getString(BundleKeys.tripModeFragment).toString()
    }

    private fun setupEditMode(){
        getTripId = arguments?.getString(BundleKeys.tripID)!!.toInt()
        getHostID = arguments?.getString(BundleKeys.tripHostID)!!.toInt()
        val getTripName = arguments?.getString(BundleKeys.tripName)!!
        val getTripCountry = arguments?.getString(BundleKeys.tripCountry)!!
        val getTripDate = arguments?.getString(BundleKeys.tripDate)!!
        val getTripType = arguments?.getString(BundleKeys.tripType)!!
        getFetchedFriends = arguments?.getString(BundleKeys.tripFriends)!!

        tv_addTrip_title.text = "Edit Trip"
        et_trip_name.setText(getTripName)
        filled_exposed_dropdown.setText(getTripCountry)
        et_trip_date.setText(getTripDate)
        if(getTripType == "Friends"){
            radioGroup.check(R.id.rb_friends)
        }else{
            radioGroup.check(R.id.rb_solo)
        }

        btn_add_trip.visibility = View.GONE
        btn_edit_trip.visibility = View.VISIBLE

    }

    private fun setupComponent(){

        btn_add_trip.isEnabled = false
        btn_edit_trip.isEnabled = false
        //=================================Country==================================
        val name = arrayOf(
                "Malaysia",
                "Singapore"
        )
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.material_spinner_item, name)
        filled_exposed_dropdown.setAdapter(adapter)
        filled_exposed_dropdown.setOnItemClickListener { parent, view, position, id ->
            Log.i("Position", position.toString())

            val selectedCountry = filled_exposed_dropdown.text
            tripCountry = selectedCountry.toString()
        }
        //=================================Country==================================

        //=================================TripName==================================
        et_trip_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateEditTextLength()
            }
        })
        //=================================TripName==================================

        //=================================TripName==================================
        et_trip_date.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateEditTextLength()
            }
        })
        //=================================TripName==================================

        //=================================TripDate==================================
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        ib_calendar.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                val selectedMonth = monthOfYear + 1
                et_trip_date.setText("" + dayOfMonth + "/" + selectedMonth + "/" + year)
            }, year, month, day)
            dpd.show()
        }

        //=================================TripDate==================================

        //=================================TripType==================================
        radioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId){
                R.id.rb_solo -> {
                    ll_search_friends.visibility = View.GONE
                    tripType = rb_solo.text.toString()
                    Log.i("TRIP TYPE", tripType)
                }
                R.id.rb_friends -> {
                    ll_search_friends.visibility = View.VISIBLE
                    tripType = rb_friends.text.toString()
                    Log.i("TRIP TYPE", tripType)

                }
            }
        }
        //=================================TripType==================================

        //=================================Search Friend Button======================
        btn_search_friend.setOnClickListener {
            searchName = et_search_friend.text.toString()
            handleSearchFriend(searchName)
        }
        //=================================Search Friend Button=======================

        //=================================Add Trip Button============================
        btn_add_trip.setOnClickListener {

            tripName = et_trip_name.text.toString()
            tripDate = et_trip_date.text.toString()

            val myArrString = Json.encodeToString(selectedTripFriend)
            Log.i("DATA", myArrString)
            Log.i("DATA CHECK", ("$tripCountry, $tripName, $tripDate, $tripType"))

            hostID = SharedPreferencesUtil.userID!!
            Log.i("TRIP TYPE", tripType)

            handleAddNewTrip(
                    hostID,
                    tripCountry,
                    tripName,
                    tripDate,
                    tripType,
                    myArrString
            )

        }
        //=================================Add Trip Button============================

        //=================================Edit Trip Button============================
        btn_edit_trip.setOnClickListener {

            tripName = et_trip_name.text.toString()
            tripDate = et_trip_date.text.toString()

            val myArrString = Json.encodeToString(selectedTripFriend)
            Log.i("DATA", myArrString)
            Log.i("DATA CHECK", ("$tripCountry, $tripName, $tripDate, $tripType"))

            handleUpdateTripDetails(
                    getTripId,
                    getHostID,
                    tripCountry,
                    tripName,
                    tripDate,
                    tripType,
                    myArrString,
            );
        }
        //=================================Edit Trip Button============================

    }

    private fun validateEditTextLength(){
        btn_add_trip.isEnabled =
                et_trip_name.text!!.isNotEmpty() && et_trip_date.text!!.isNotEmpty()

        btn_edit_trip.isEnabled =
                et_trip_name.text!!.isNotEmpty() && et_trip_date.text!!.isNotEmpty()
    }

    private fun setupAdapter(friendList: ArrayList<UserModel.User>) {

        rv_search_friend_results.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_search_friend_results.setHasFixedSize(true)
        adapter = SearchFriendResultAdapter(requireContext(), friendList)
        rv_search_friend_results.adapter = adapter

        adapter.onItemClick = {
            if(!selectedTripFriend.contains(it)){
                selectedTripFriend.add(it)
                val a = friendList.filter { user -> (user.username != it.username) }
                friendList.clear()
                friendList.addAll(a)
                adapter.notifyDataSetChanged()
                setupFriendAddedAdapter(selectedTripFriend)

            }else{
                Log.i("INVALID", "FRIEND ALREADY ADDED !")
            }
        }
    }

    private fun setupFriendAddedAdapter(friendList: ArrayList<UserModel.User>){

        rv_search_friend_added.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_search_friend_added.setHasFixedSize(true)
        addedFriendAdapter = FriendAddedAdapter(requireContext(), friendList)
        rv_search_friend_added.adapter = addedFriendAdapter

        addedFriendAdapter.onItemClick = {
            friendList.remove(it)
            selectedTripFriend.clear()
            selectedTripFriend.addAll(friendList)
            addedFriendAdapter.notifyDataSetChanged()
        }

    }

    private fun handleSearchFriend(username: String){
        val subscribe = viewModel.handleSearchFriend(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    Log.i("Results:", it.size.toString())
                    friendList.clear()
                    friendList.addAll(it)
                    setupAdapter(friendList)
                    adapter.notifyDataSetChanged()
                }, { err ->
                    var msg = err.localizedMessage
                    Log.i("DATA", err.toString())
                })
        subscription.add(subscribe)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.clear()
    }

    private fun handleAddNewTrip(
            hostId: Int,
            country: String,
            tripName: String,
            tripDate: String,
            tripType: String,
            friendList: String){

        val subscribe = viewModel.handleAddTrip(
                hostId = hostId,
                country = country,
                tripName = tripName,
                tripDate = tripDate,
                tripType = tripType,
                friendList = friendList
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    Log.i("DATA", "TRIP ADDED ! ${it.toString()}")
                    handleGetUserTripList()
                    val snackbar = Snackbar.make(requireView(), "Trip Added!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                    navController.navigate(R.id.action_addTripFragment_to_dashboardFragment)
                }, { err ->
                    var msg = err.localizedMessage
                    Log.i("DATA", msg.toString())
                })
        subscription.add(subscribe)
    }

    private fun handleUpdateTripDetails(
            tripId: Int,
            hostId: Int,
            country: String,
            tripName: String,
            tripDate: String,
            tripType: String,
            friendList: String
    ) {

        val subscribe = viewModel.handleUpdateTripDetails(
                tripId = tripId,
                hostId = hostId,
                country = country,
                tripName = tripName,
                tripDate = tripDate,
                tripType = tripType,
                friendList = friendList
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    Log.i("DATA", "TRIP UPDATED ! ${it.toString()}")
                    handleGetUserTripList()
                    val snackbar = Snackbar.make(requireView(), "Trip Details Updated!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                    navController.navigate(R.id.action_addTripFragment_to_dashboardFragment)
                }, { err ->
                    var msg = err.localizedMessage
                    Log.i("DATA", msg.toString())
                })
        subscription.add(subscribe)
    }


    private fun handleGetUserTripList() {
        val subscribe = viewModel.handleUserTripList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.i("ORIGINAL TRIP LIST", it.toString())

                //val decodedJson = Json.decodeFromString<List<UserModel.User>>(friendListTrip)
                //Log.i("DECODED DATA", decodedJson[0].username)
                for (trip in it) {

                    val decodedJson = Json.decodeFromString<List<UserModel.User>>(trip.friendList)
                    tripId.clear()
                    tripId.add(trip.tripId)
                    val tripIdString = tripId.joinToString()

                    for (user in decodedJson) {

                        //FILTER UPCOMING TRIP ID
                        if (user.upcomingTrip.length == 1) {

                            val currentTripID: ArrayList<String> = arrayListOf()
                            currentTripID.add(user.upcomingTrip)
                            currentTripID.add(tripIdString)
                            val finalTripID: String = currentTripID.joinToString()

                            handleUpdateFriendsUpcomingTrip(user.userID, finalTripID)

                        } else if (user.upcomingTrip.contains(", ") || user.upcomingTrip.length > 1) {

                            val currentTripID: List<String> = user.upcomingTrip.split(", ")
                            val filteredTrip = currentTripID.toMutableList()
                            filteredTrip.add(tripIdString)
                            val finalTripID: String = filteredTrip.joinToString()

                            handleUpdateFriendsUpcomingTrip(user.userID, finalTripID)


//                                 val extractedTripID: ArrayList<String> = arrayListOf()
//                                 extractedTripID.add(user.upcomingTrip)
//                                 Log.i("EXTRACTEDTRIPID", extractedTripID.toString())
//
//
//                                 val currentTripID: MutableList<String> = extractedTripID
//                                 currentTripID.add(tripIdString)

                            //val finalTripID: String = currentTripID.joinToString()
                        } else {
                            handleUpdateFriendsUpcomingTrip(user.userID, tripIdString)
                        }
                        Log.i("LIST OF STRING FINAL", "${user.username} - ${user.upcomingTrip}")

                    }


                }

            }, { err ->
                var msg = err.localizedMessage
                Log.i("ERROR", msg.toString())
            })
        subscription.add(subscribe)
    }

    private fun handleUpdateFriendsUpcomingTrip(userId: Int, upcomingTrips: String){

        val subscribe = viewModel.handleUpdateFriendsUpcomingTrip(userId, upcomingTrips)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i("SUCCESS", "alerted !")

            }, { err ->
                var msg = err.localizedMessage
                Log.i("ERROR", msg.toString())
            })
        subscription.add(subscribe)
    }

}