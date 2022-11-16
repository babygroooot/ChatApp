package com.example.chatapp.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityLoginBinding
import com.google.android.material.elevation.SurfaceColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this,R.color.colorWhite)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        supportActionBar?.hide()
        mAuth = Firebase.auth

        setUpAction()

    }

    private fun setUpAction(){
        binding.signupBtn.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()

            logIn(email,password)
        }
    }

    private fun logIn(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){task ->
            if (task.isSuccessful){
                val intent = Intent(this, ListChatActivity::class.java)
                finish()
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
            }
        }
    }

}