package com.example.chatapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.EdgeEffect
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.adapter.ListChatAdapter
import com.example.chatapp.R
import com.example.chatapp.RecyclerViewItemClick
import com.example.chatapp.model.UserModel
import com.example.chatapp.databinding.ActivityListChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class ListChatActivity : AppCompatActivity(), RecyclerViewItemClick {

    private lateinit var binding: ActivityListChatBinding
    private val userList = ArrayList<UserModel>()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbReference: DatabaseReference
    private lateinit var listChatAdapter: ListChatAdapter
    private var doubleBackpressToExit = false
    private val onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            handleBackPressed()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorMain)))
        window.statusBarColor = ContextCompat.getColor(this,R.color.colorMain)
        mAuth = Firebase.auth
        mDbReference = Firebase.database.reference

        setUpRecyclerView()
        setUpDatabase()
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    private fun handleBackPressed(){
        if (doubleBackpressToExit){
            finish()
        }
        doubleBackpressToExit = true
        Toast.makeText(this@ListChatActivity, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        delayFunction({
            doubleBackpressToExit = false
        }, 2000)
    }


    private fun delayFunction(function: () -> Unit, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed(function, delay)
    }

    private fun setUpRecyclerView(){
        listChatAdapter = ListChatAdapter(this@ListChatActivity,userList,this@ListChatActivity)
        binding.listchatRv.apply {
            adapter = listChatAdapter
            layoutManager = LinearLayoutManager(this@ListChatActivity,RecyclerView.VERTICAL,false)
        }
        binding.listchatRv.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                return EdgeEffect(view.context).apply { color = ContextCompat.getColor(this@ListChatActivity,R.color.colorMain) }
            }
        }
    }

    private fun setUpDatabase(){
        mDbReference.child("user").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(UserModel::class.java)
                    userList.add(currentUser!!)
                }
                listChatAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListChatActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onClick(position: Int) {
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("OBJ",Gson().toJson(userList[position]))
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout){
            mAuth.signOut()
            val intent = Intent(this,LoginActivity::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }

}