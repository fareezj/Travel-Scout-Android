package com.wolf.travelscout.ui.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.wolf.travelscout.R
import com.wolf.travelscout.databinding.FragmentDashboardBinding
import com.wolf.travelscout.util.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private var subscription = CompositeDisposable()


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

        tv_username.text = SharedPreferencesUtil.username

        handlePrivatePage()

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
}