package com.grocery.app.HomePage.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R

class ParentViewHolder(view: View): RecyclerView.ViewHolder(view) {

    var itemTitle: TextView
    var horizontal_recycler_view: RecyclerView
    var btnMore: TextView

    init {
        itemTitle  = view.findViewById(R.id.itemTitle) as TextView
        btnMore  = view.findViewById(R.id.btnMore) as TextView
        horizontal_recycler_view = view.findViewById(R.id.recycler_view_horizontal) as RecyclerView

    }
}