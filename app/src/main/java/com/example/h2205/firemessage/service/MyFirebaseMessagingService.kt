package com.example.h2205.firemessage.service

import android.util.Log
import com.example.h2205.firemessage.util.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.anko.toast

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(newRegistrationToken: String?) {
        toast("notification!!")

        if ( FirebaseAuth.getInstance().currentUser != null ){

            addTokenToFireStore( newRegistrationToken )

        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
            toast("notification!!")
        Log.e("FCM", "FCM message received!")
        if ( remoteMessage.notification != null ){

            //TODO: Show Notification
            Log.e("FCM", "FCM message received!")

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