package com.example.h2205.firemessage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import com.example.h2205.firemessage.model.ImageMessage
import com.example.h2205.firemessage.model.MessageType
import com.example.h2205.firemessage.model.TextMessage
import com.example.h2205.firemessage.util.FirestoreUtil
import com.example.h2205.firemessage.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*

private const val RC_SELECT_IMAGE = 2

class ChatActivity : AppCompatActivity() {

    private lateinit var currentChannelId : String

    private lateinit var  messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = intent.getStringExtra( AppConstants.USER_NAME )

        val otherUserId = intent.getStringExtra( AppConstants.USER_ID )

        FirestoreUtil.getOrCreateChatChannel( otherUserId ){ channelId ->

            currentChannelId = channelId

            messagesListenerRegistration = FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            imageView_send.setOnClickListener {

                val messageToSend = TextMessage( editText_message.text.toString(), Calendar.getInstance().time, FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT )

                editText_message.setText("")

                FirestoreUtil.sendMessage( messageToSend, channelId )

            }

            fab_send_image.setOnClickListener {

                val intent = Intent().apply {

                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra( Intent.EXTRA_MIME_TYPES, arrayOf( "image/jpeg", "image/png" ) )

                }
                startActivityForResult(Intent.createChooser( intent, "Select image" ), RC_SELECT_IMAGE)

            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if ( requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null ){

            val selectedImagePath = data.data

            val selectedImageBitMap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()

            selectedImageBitMap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes){imagePath ->

                val messageToSend = ImageMessage( imagePath, Calendar.getInstance().time, FirebaseAuth.getInstance().currentUser!!.uid )

                FirestoreUtil.sendMessage( messageToSend, currentChannelId )
            }


        }

    }

    private fun updateRecyclerView(messages: List<Item> ){

        fun init(){

            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {

                    messagesSection = Section( messages )
                    this.add( messagesSection )
                }
            }

            shouldInitRecyclerView = false

        }

        fun updateItems() = messagesSection.update( messages )


        if ( shouldInitRecyclerView ){
            init()
        }else{
            updateItems()
        }

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter!!.itemCount -1 )

    }

}
