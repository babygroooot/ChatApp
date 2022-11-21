package com.example.chatapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.chatapp.databinding.ChatReceiverItemLayoutBinding
import com.example.chatapp.databinding.ChatSenderItemLayoutBinding
import com.example.chatapp.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatAdapter(val context: Context, private val messageList: ArrayList<MessageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        const val TYPE_SENDER = 0
        const val TYPE_RECEIVER = 1
    }

    inner class SenderViewHolder(val binding: ChatSenderItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){}
    inner class ReceiverViewHolder(val binding: ChatReceiverItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_SENDER -> {
                SenderViewHolder(ChatSenderItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            TYPE_RECEIVER -> {
                ReceiverViewHolder(ChatReceiverItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            else -> {
                throw IllegalArgumentException("Wrong Type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is SenderViewHolder -> {
                if (messageList[position].message != null){
                    holder.binding.senderIv.visibility = View.GONE
                    holder.binding.senderTv.text = messageList[position].message
                }
                else{
                    holder.binding.senderTv.visibility = View.GONE
                    holder.binding.senderIv.visibility = View.VISIBLE
                    Glide.with(context).load(messageList[position].image).diskCacheStrategy(
                        DiskCacheStrategy.ALL).into(holder.binding.senderIv)
                }
            }

            is ReceiverViewHolder -> {
                if (messageList[position].message != null){
                    holder.binding.receiverIv.visibility = View.GONE
                    holder.binding.receiverTv.text = messageList[position].message
                }
                else{
                    holder.binding.receiverTv.visibility = View.GONE
                    holder.binding.receiverIv.visibility = View.VISIBLE
                    Glide.with(context).load(messageList[position].image).diskCacheStrategy(
                        DiskCacheStrategy.ALL).into(holder.binding.receiverIv)
                }
            }
        }
/*if (FirebaseAuth.getInstance().currentUser?.uid == messageList[position].senderId){
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
        }*/

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (Firebase.auth.currentUser?.uid == messageList[position].senderId) TYPE_SENDER
        else TYPE_RECEIVER
    }

}
