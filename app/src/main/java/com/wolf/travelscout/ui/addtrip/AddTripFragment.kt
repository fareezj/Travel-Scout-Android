package com.wolf.travelscout.ui.addtrip

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import com.wolf.travelscout.R
import kotlinx.android.synthetic.main.fragment_add_trip.*

class AddTripFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        radioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId){
                R.id.rb_solo -> ll_search_friends.visibility = View.GONE
                R.id.rb_friends -> ll_search_friends.visibility = View.VISIBLE
            }
        }

    }

}