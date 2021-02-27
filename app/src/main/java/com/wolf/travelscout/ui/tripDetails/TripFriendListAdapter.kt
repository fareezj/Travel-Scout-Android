package com.wolf.travelscout.ui.tripDetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wolf.travelscout.R
import com.wolf.travelscout.data.model.UserModel
import kotlinx.android.synthetic.main.search_friend_result_item.view.*

class TripFriendListAdapter (val context: Context, var items: List<UserModel.User>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClick: ((UserModel.User) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TripFriendViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.search_friend_result_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TripFriendViewHolder){
            holder.itemView.tv_search_friend_name.text = items[position].username

            Glide.with(context)
                    .load(items[position].profileImage)
                    .placeholder(R.drawable.ic_baseline_add_reaction_24)
                    .into(holder.itemView.iv_profile_image_search)

            holder.itemView.setOnClickListener {
                onItemClick?.invoke(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class TripFriendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }


}