package com.wolf.travelscout.ui.addtrip

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.UserModel
import kotlinx.android.synthetic.main.search_friend_result_item.view.*

class SearchFriendResultAdapter (context: Context?, var items: ArrayList<UserModel.User>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchFriendViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.search_friend_result_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is SearchFriendViewHolder){
            holder.itemView.tv_search_friend_name.text = items[position].username
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class SearchFriendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }


}