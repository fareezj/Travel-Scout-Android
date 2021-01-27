package com.wolf.travelscout.ui.addtrip

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.UserModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_add_trip.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class AddTripFragment : Fragment() {

    private var subscription = CompositeDisposable()
    private lateinit var viewModel: AddTripViewModel
    private lateinit var adapter: SearchFriendResultAdapter
    private lateinit var addedFriendAdapter: FriendAddedAdapter
    private var friendList: ArrayList<UserModel.User> = arrayListOf()
    private var searchName: String = ""
    private var selectedTripFriend: ArrayList<UserModel.User> = arrayListOf()
    private var tripCountry: String = "Malaysia"
    private var tripName: String = ""
    private var tripDate: String = ""
    private var tripType: String = ""
    private var hostID: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AddTripViewModel::class.java)

        setupComponent()

        btn_deserialize.setOnClickListener {

            val myArrString = Json.encodeToString(selectedTripFriend)
            val deserializer = Json.decodeFromString<List<UserModel.User>>(myArrString)
        }

    }

    private fun setupComponent(){

        btn_add_trip.isEnabled = false
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
                }
                R.id.rb_friends -> {
                    ll_search_friends.visibility = View.VISIBLE
                    tripType = rb_friends.text.toString()
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

            handleAddNewTrip(
                    tripCountry,
                    tripName,
                    tripDate,
                    tripType,
                    myArrString
            )

        }
        //=================================Add Trip Button============================

    }

    private fun validateEditTextLength(){
        btn_add_trip.isEnabled =
                et_trip_name.text!!.isNotEmpty() &&
                        et_trip_date.text!!.isNotEmpty()
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
                },{ err -> var msg = err.localizedMessage
                    Log.i("DATA", err.toString())
                })
        subscription.add(subscribe)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.clear()
    }

    private fun handleAddNewTrip(
            country: String,
            tripName: String,
            tripDate: String,
            tripType: String,
            friendList: String){

        val subscribe = viewModel.handleAddTrip(
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

                }, { err ->
                    var msg = err.localizedMessage
                    Log.i("DATA", msg.toString())
                })
        subscription.add(subscribe)
    }

}