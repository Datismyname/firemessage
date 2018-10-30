package com.example.h2205.firemessage.recyclerview.item

import android.content.Context
import com.example.h2205.firemessage.R
import com.example.h2205.firemessage.model.TextMessage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class TextMessageItem(val message:TextMessage, context: Context): Item(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLayout() = R.layout.item_text_message
}