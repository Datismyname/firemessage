package com.example.h2205.firemessage.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.h2205.firemessage.AppConstants
import com.example.h2205.firemessage.ChatActivity

import com.example.h2205.firemessage.R
import com.example.h2205.firemessage.recyclerview.item.PersonItem
import com.example.h2205.firemessage.util.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_people.*
import org.jetbrains.anko.support.v4.startActivity


class PeopleFragment : Fragment() {

    private lateinit var userListenerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true

    private lateinit var peopleSection: Section

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        userListenerRegistration = FirestoreUtil.addUsersListener( this.activity!! , this::updateRecyclerView)

        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        FirestoreUtil.removeListener( userListenerRegistration )
        shouldInitRecyclerView = true

    }


    private fun updateRecyclerView( items: List<Item> ){

        fun init(){
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add( peopleSection )
                    setOnItemClickListener( onItemClick )
                }
            }

            shouldInitRecyclerView = false
        }

        fun update() = peopleSection.update(items)

        if ( shouldInitRecyclerView ){
            init()
        }else{
            update()
        }

    }

    private val onItemClick = OnItemClickListener{ item, view ->

        if ( item is PersonItem ){

            startActivity<ChatActivity>(
                AppConstants.USER_NAME to item.person.name,
                AppConstants.USER_ID to item.userId
            )

        }

    }


}
