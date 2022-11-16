package com.example.chatapp.Activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EdgeEffect
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ChatAdapter
import com.example.chatapp.MessageModel
import com.example.chatapp.R
import com.example.chatapp.UserModel
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private var messageList = ArrayList<MessageModel>()
    private lateinit var mDbRef: DatabaseReference
    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private lateinit var storage: FirebaseStorage
    private var selectedImage: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorMain)
        setUpAction()
        setUpData()
        setUpRecyclerView()
        setUpCallback()
    }

    private fun setUpCallback() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                selectedImage = it
                val senderId = Firebase.auth.currentUser!!.uid
                val storageReference = storage.reference
                val name = UUID.randomUUID().toString()
                val fileReference = storageReference.child(name)
                val dataRef = storageReference.child("/$name")
                fileReference.putFile(selectedImage!!).addOnSuccessListener {
                    dataRef.downloadUrl.addOnSuccessListener { url ->
                        val messageObj = MessageModel(senderId = senderId, image = url.toString())
                        mDbRef.child("chat").child(senderRoom!!).child("message").push()
                            .setValue(messageObj).addOnSuccessListener {
                                mDbRef.child("chat").child(receiverRoom!!).child("message").push()
                                    .setValue(messageObj)
                            }
                    }
                }
            }
        }
    }

    private fun setUpAction() {
        val intentExtra = intent.getStringExtra("OBJ")
        val userObj = Gson().fromJson(intentExtra, UserModel::class.java)
        val senderId = Firebase.auth.currentUser!!.uid
        val receiverId = userObj.uid
        mDbRef = Firebase.database.reference
        storage = Firebase.storage
        binding.nameTv.text = userObj.name
        binding.backpressIv.setOnClickListener {
            finish()
        }
        senderRoom = receiverId + senderId
        receiverRoom = senderId + receiverId
        binding.sendIv.setOnClickListener {
            val message = binding.chatTextbox.text.toString()
            val messageObj = MessageModel(message, senderId)

            if (!binding.chatTextbox.text.isNullOrBlank()) {
                mDbRef.child("chat").child(senderRoom!!).child("message").push()
                    .setValue(messageObj).addOnSuccessListener {
                        mDbRef.child("chat").child(receiverRoom!!).child("message").push()
                            .setValue(messageObj)
                    }
                binding.chatTextbox.setText("")
            }
        }
        binding.rvScroll.setOnClickListener {
            binding.chatRv.smoothScrollToPosition(messageList.size - 1)
        }

        binding.attachmentIv.setOnClickListener {
            activityResultLauncher.launch("image/*")
        }

    }

    private fun setUpRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        chatAdapter = ChatAdapter(this, messageList)
        binding.chatRv.apply {
            adapter = chatAdapter
            layoutManager = linearLayoutManager
            recycledViewPool.setMaxRecycledViews(0, 0)
            setItemViewCacheSize(20)
            setHasFixedSize(true)
        }
        binding.chatRv.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                return EdgeEffect(view.context).apply {
                    color = ContextCompat.getColor(this@MainActivity, R.color.colorMain)
                }
            }
        }
        binding.chatRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val offset = recyclerView.computeVerticalScrollOffset()
                val range = recyclerView.computeVerticalScrollRange()
                val extend = recyclerView.computeVerticalScrollExtent()
                val max = offset + extend
                Log.d("xxxxxxxxxxxxxx","Max: $max")
                Log.d("xxxxxxxxxxxxxx","Range: $range")
                if (max < range - 3000) {
                    binding.rvScroll.visibility = View.VISIBLE
                }
                /*if (range == max) {
                    binding.rvScroll.visibility = View.GONE
                }*/
                else{
                    binding.rvScroll.visibility = View.GONE
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpData() {
        mDbRef.child("chat").child(senderRoom!!).child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(MessageModel::class.java)
                        messageList.add(message!!)
                    }
                    chatAdapter.notifyDataSetChanged()
                    binding.chatRv.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}