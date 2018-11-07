package com.example.h2205.firemessage.model

import java.util.*

data class TextMessage(
        val text: String, override val time: Date, override val senderId: String,
        override val reciptientId: String, override val senderName: String, override val type: String = MessageType.TEXT):
        Message {

    constructor() : this("",Date(0),"","","")

}