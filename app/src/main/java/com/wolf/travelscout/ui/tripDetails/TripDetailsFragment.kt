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
    private var tripHostId: String? = ""
    private var tripName: String? = ""
    private var tripCountry: String? = ""
    private var tripDate: String? = ""
    private var tripType: String? = ""

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
        setupView()
    }

    private fun getDataFromArguments() {
        tripId = arguments?.getString(BundleKeys.tripID)
        tripHostId = arguments?.getString(BundleKeys.tripHostID)
        tripName = arguments?.getString(BundleKeys.tripName)
        tripCountry = arguments?.getString(BundleKeys.tripCountry)
        tripDate = arguments?.getString(BundleKeys.tripDate)
        tripType = arguments?.getString(BundleKeys.tripType)
    }

    private fun setupView() {
        tv_tripId.text = tripId
        tv_tripHostId.text = tripHostId
        tv_tripName.text = tripName
        tv_tripCountry.text = tripCountry
        tv_tripDate.text = tripDate
        tv_tripType.text = tripType
    }
}