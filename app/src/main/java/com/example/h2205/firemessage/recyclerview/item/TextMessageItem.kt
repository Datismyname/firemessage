package com.example.h2205.firemessage.recyclerview.item

import android.content.Context
import com.example.h2205.firemessage.R
import com.example.h2205.firemessage.model.TextMessage
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*


class TextMessageItem(val message:TextMessage, context: Context): MessageItem(message){

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.textView_message_text.text = message.text
        super.bind(viewHolder, position)

    }



    override fun getLayout() = R.layout.item_text_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is TextMessageItem) return false
        if ( this.message != other.message ) return false

        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs( other as TextMessageItem )
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }

}