package com.wolf.travelscout.ui.tripDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wolf.travelscout.R
import com.wolf.travelscout.util.BundleKeys
import kotlinx.android.synthetic.main.fragment_trip_details.*


class TripDetailsFragment : Fragment() {

    private var tripId: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromArguments()
    }

    private fun getDataFromArguments() {
        tripId = arguments?.getString(BundleKeys.tripID)
        tv_tripId.text = tripId
    }
}