package com.example.h2205.firemessage.model

data class User(val name:String, val bio:String, val profilePicturePath:String?) {

    constructor():this("","",null)



}
