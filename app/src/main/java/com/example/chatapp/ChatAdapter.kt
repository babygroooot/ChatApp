package com.example.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.chatapp.databinding.ChatItemLayoutBinding
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(val context: Context, private val messageList: ArrayList<MessageModel>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    companion object{
        const val TYPE_SENDER = 0
        const val TYPE_RECEIVER = 1
    }

    inner class ViewHolder(val binding: ChatItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ChatItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (FirebaseAuth.getInstance().currentUser?.uid == messageList[position].senderId){
            if (messageList[position].message != null){
                holder.binding.receiverTv.visibility = View.GONE
                holder.binding.receiverIv.visibility = View.GONE
                holder.binding.senderIv.visibility = View.GONE
                holder.binding.senderTv.text = messageList[position].message
            }
            else{
                holder.binding.receiverTv.visibility = View.GONE
                holder.binding.receiverIv.visibility = View.GONE
                holder.binding.senderTv.visibility = View.GONE
                val imageUrl = messageList[position].image
                Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.senderIv)
            }
        }
        else{
            if (messageList[position].message != null){
                holder.binding.senderTv.visibility = View.GONE
                holder.binding.receiverIv.visibility = View.GONE
                holder.binding.senderIv.visibility = View.GONE
                holder.binding.receiverTv.text = messageList[position].message
            }
            else{
                holder.binding.receiverTv.visibility = View.GONE
                holder.binding.senderIv.visibility = View.GONE
                holder.binding.senderTv.visibility = View.GONE
                val imageUrl = messageList[position].image
                Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.receiverIv)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}