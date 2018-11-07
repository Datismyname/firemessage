package com.example.h2205.firemessage.model

import java.util.*

object MessageType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message {

    val time: Date
    val senderId: String
    val reciptientId: String
    val senderName: String
    val type: String

}