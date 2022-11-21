package com.example.chatapp

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object BindingAdapter {

    @androidx.databinding.BindingAdapter("setImage")
    fun setImage(imageView: ImageView,url: String){
        Glide.with(imageView.context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
    }

}