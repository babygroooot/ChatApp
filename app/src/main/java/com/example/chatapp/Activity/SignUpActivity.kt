package com.example.chatapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.chatapp.R
import com.example.chatapp.UserModel
import com.example.chatapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        supportActionBar?.hide()
        mAuth = Firebase.auth

        setUpAction()

    }

    private fun setUpAction(){
        binding.loginBtn.setOnClickListener {
            finish()
        }

        binding.signupBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()

            signUp(username,email,password)
        }
    }

    private fun signUp(username: String, email: String, password: String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDataBase(username,email,mAuth.currentUser?.uid!!)
                    val intent = Intent(this, ListChatActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
     }

    private fun addUserToDataBase(username: String, email: String, uid: String) {
        mDbRef = Firebase.database.reference
        mDbRef.child("user").child(uid).setValue(UserModel(username,email,uid))
    }

}