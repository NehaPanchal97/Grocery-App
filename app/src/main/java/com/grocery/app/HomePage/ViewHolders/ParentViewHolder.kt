package com.grocery.app.HomePage.ViewHolders

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.HomePage.Adapters.MainAdapterVertical
import com.grocery.app.HomePage.DataModel.ItemData
import com.grocery.app.HomePage.DataModel.ItemGroup
import com.grocery.app.R

class ParentViewHolder(view: View): RecyclerView.ViewHolder(view) {

    var itemTitle: TextView
    var horizontal_recycler_view: RecyclerView
    var btnMore: Button
    lateinit var recyclerViewAdapter: MainAdapterVertical

    //initializing horizontal recycler with header of home page i.e 1 and 3 row of homepage

    init {
        itemTitle  = view.findViewById(R.id.itemTitle) as TextView
        btnMore  = view.findViewById(R.id.btnMore) as Button
        horizontal_recycler_view = view.findViewById(R.id.recycler_view_horizontal) as RecyclerView

    }
}