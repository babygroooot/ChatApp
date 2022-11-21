package com.example.chatapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySplashScreenBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = Firebase.auth
        binding.progressbar.show()
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this,R.color.colorWhite)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
    }

    override fun onStart() {
        super.onStart()
        setUpAction()
    }

    private fun setUpAction(){
        val handler = Handler(Looper.getMainLooper())
        val currentUser = mAuth.currentUser
        handler.postDelayed({
            if (currentUser != null){
                finish()
                startActivity(Intent(this,ListChatActivity::class.java))
            }
            else{
                finish()
                startActivity(Intent(this,LoginActivity::class.java))
            }
            binding.progressbar.hide()
        },3000)
    }

}