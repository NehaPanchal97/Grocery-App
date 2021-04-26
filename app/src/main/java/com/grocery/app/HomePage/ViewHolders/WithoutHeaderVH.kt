package com.grocery.app.HomePage.ViewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.HomePage.Interface.IItemClickListener
import com.grocery.app.R

@Suppress("DEPRECATION")
class WithoutHeaderVH(view : View): RecyclerView.ViewHolder(view){

    var horizontal_rv_without_header: RecyclerView

    init {

        horizontal_rv_without_header = view.findViewById(R.id.without_header_rv) as RecyclerView

    }

}