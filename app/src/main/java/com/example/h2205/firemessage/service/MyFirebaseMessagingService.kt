package com.example.h2205.firemessage.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.h2205.firemessage.util.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.anko.toast

class MyFirebaseMessagingService: FirebaseMessagingService(){

    override fun onNewToken(newRegistrationToken: String?) {

        if ( FirebaseAuth.getInstance().currentUser != null ){

            addTokenToFireStore( newRegistrationToken )

        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if ( remoteMessage.notification != null ){

            //TODO: Show Notification if we are not in the chat channel from which the incoming message was sent
            Log.e("FCM", remoteMessage.data.toString() )

        }


    }



    companion object {
        fun addTokenToFireStore(newRegistrationToken : String?){

            if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

            FirestoreUtil.getFCMRegistrationTokens { tokens ->

                if ( tokens.contains( newRegistrationToken ) ) return@getFCMRegistrationTokens

                tokens.add( newRegistrationToken )

                FirestoreUtil.setFCMRegistrationTokens( tokens )

            }

        }
    }


}