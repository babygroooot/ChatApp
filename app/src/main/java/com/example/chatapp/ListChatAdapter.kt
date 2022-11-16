package com.example.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ListChatItemLayoutBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ListChatAdapter(private val context:Context, private val userList: ArrayList<UserModel>, val recyclerViewItemClick: RecyclerViewItemClick) :
    RecyclerView.Adapter<ListChatAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ListChatItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListChatItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (userList[position].uid == Firebase.auth.currentUser!!.uid){
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
        holder.binding.model = userList[position]
        holder.itemView.setOnClickListener{
            recyclerViewItemClick.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}