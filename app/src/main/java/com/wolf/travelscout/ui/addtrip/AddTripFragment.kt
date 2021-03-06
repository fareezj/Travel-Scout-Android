package com.wolf.travelscout.ui.addtrip

import android.os.Bundle
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
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.UserModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_add_trip.*

class AddTripFragment : Fragment() {

    private var subscription = CompositeDisposable()
    private lateinit var viewModel: AddTripViewModel
    private lateinit var adapter: SearchFriendResultAdapter
    private var friendList: ArrayList<UserModel.User> = arrayListOf()
    private var searchName: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AddTripViewModel::class.java)

        setupComponent()

    }

    private fun setupComponent(){


        // Country Spinner
        val name = arrayOf(
                "Malaysia",
                "Singapore"
        )

        val adapter = ArrayAdapter<String>(requireContext(), R.layout.material_spinner_item, name)
        filled_exposed_dropdown.setAdapter(adapter)
        filled_exposed_dropdown.setOnItemClickListener { parent, view, position, id ->
            Log.i("Position", position.toString())

            val a = filled_exposed_dropdown.text
            Log.i("Position", a.toString())
        }

        btn_search_friend.setOnClickListener {
            searchName = et_search_friend.text.toString()
            handleSearchFriend(searchName)
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId){
                R.id.rb_solo -> ll_search_friends.visibility = View.GONE
                R.id.rb_friends -> ll_search_friends.visibility = View.VISIBLE
            }
        }

    }

    private fun setupAdapter(friendList: ArrayList<UserModel.User>) {

        rv_search_friend_results.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_search_friend_results.setHasFixedSize(true)
        adapter = SearchFriendResultAdapter(context, friendList)
        rv_search_friend_results.adapter = adapter
        adapter.notifyDataSetChanged()
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

}