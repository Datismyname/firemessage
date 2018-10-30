package com.example.h2205.firemessage.model

data class ChatChannel(val userIds: MutableList<String>) {

    constructor() : this(mutableListOf())

}