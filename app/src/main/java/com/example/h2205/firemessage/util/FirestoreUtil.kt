package com.example.h2205.firemessage.util

import android.content.Context
import android.util.Log
import com.example.h2205.firemessage.model.ChatChannel
import com.example.h2205.firemessage.model.MessageType
import com.example.h2205.firemessage.model.TextMessage
import com.example.h2205.firemessage.model.User
import com.example.h2205.firemessage.recyclerview.item.PersonItem
import com.example.h2205.firemessage.recyclerview.item.TextMessageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item

object FirestoreUtil {

    private val firestoreInstence: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference get() =
        firestoreInstence.document("users/${FirebaseAuth.getInstance().currentUser?.uid ?: throw NullPointerException("UID is null")}")

    private val chatChannelsCollectionReference = firestoreInstence.collection("chatChannels")


    fun initCurrentUserIfFirstTime(onComplete: () -> Unit){

        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()){
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName ?: "", "", "")
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }else{
                onComplete()
            }
        }

    }

    fun updateCurrentUser( name:String = "", bio:String = "", profilePicturePath:String ? = null){

        val userFieldMap = mutableMapOf<String, Any>()

        if ( name.isNotBlank() ) userFieldMap["name"] = name
        if ( bio.isNotBlank() ) userFieldMap["bio"] = bio
        if ( profilePicturePath != null ) userFieldMap["profilePicturePath"] = profilePicturePath

        currentUserDocRef.update( userFieldMap )

    }

    fun getCurrentUser( onComplete: (User) -> Unit ){

        currentUserDocRef.get().addOnSuccessListener {

            onComplete( it.toObject( User::class.java ) )

        }
    }


    fun addUsersListener( context: Context, onListen: (List<Item>) -> Unit ) : ListenerRegistration {

        return firestoreInstence.collection("users")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if ( firebaseFirestoreException != null ){
                        Log.e("FIRESTORE", "User listener error.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }

                    val items = mutableListOf<Item>()

                    querySnapshot.documents.forEach {

                        if ( it.id != FirebaseAuth.getInstance().currentUser?.uid ){
                            items.add( PersonItem( it.toObject(User::class.java), it.id, context ) )
                        }

                    }
                    onListen(items)

                }
    }


    fun removeListener( registration: ListenerRegistration ) = registration.remove()

    fun getOrCreateChatChannel( otherUserId: String, onComplete:(channelId: String) -> Unit ){

        currentUserDocRef.collection( "engagedChatChannels" ).document( otherUserId ).get().addOnSuccessListener {

            // if chat channel already exists which mean we already chatting with other user
            if ( it.exists() ){
                onComplete( it["channelId"] as String ) // get the field "channelId" from DocumentSnapshot
                return@addOnSuccessListener
            }

            // otherwise if chat channel doesn't exists create new chat channel

            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

            val newChannel = chatChannelsCollectionReference.document()

            newChannel.set( ChatChannel( mutableListOf( currentUserId, otherUserId ) ) )

            // save chat channel id in both users who chat together

            currentUserDocRef
                    .collection( "engagedChatChannels" )
                    .document( otherUserId )
                    .set( mapOf( "channelId" to newChannel.id ) ) // the newChannel.id is the id of channel document inside Firestore

            firestoreInstence.collection("users").document( otherUserId )
                    .collection( "engagedChatChannels" )
                    .document( currentUserId )
                    .set( mapOf( "channelId" to newChannel.id ) )

            onComplete( newChannel.id )


        }

    }


    fun addChatMessagesListener( channelId: String, context: Context, onListen: (List<Item>) -> Unit ) : ListenerRegistration{

        return chatChannelsCollectionReference.document( channelId ).collection("messages")
                .orderBy("time")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    if ( firebaseFirestoreException != null ){
                        Log.e("FIRESTORE", "ChatMessageListener error.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }

                    val items = mutableListOf<Item>()

                    querySnapshot.forEach {

                        if ( it["type"] == MessageType.TEXT ){

                            items.add( TextMessageItem( it.toObject(TextMessage::class.java), context ) )

                        }else{
                            TODO("add image message")
                        }

                    }

                    onListen(items)


                }

    }


}