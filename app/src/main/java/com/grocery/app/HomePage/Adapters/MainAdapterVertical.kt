package com.grocery.app.HomePage.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.HomePage.DataModel.ItemData
import com.grocery.app.HomePage.DataModel.ItemGroup
import com.grocery.app.HomePage.ViewHolders.ParentViewHolder
import com.grocery.app.HomePage.ViewHolders.WithoutHeaderVH
import com.grocery.app.R

class MainAdapterVertical(private val context: Context,
                          private var dataList: ArrayList<ItemGroup>
                         ):
    RecyclerView.Adapter<RecyclerView.ViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       if(viewType== VIEW_GROUP_LAYOUT){
           return ParentViewHolder(
                   LayoutInflater.from(context).inflate(
                           R.layout.category_group, parent, false
                   )
           )
       }else if(viewType== VIEW_WITHOUT_HEADER){
           return WithoutHeaderVH(
                   LayoutInflater.from(context).inflate(
                           R.layout.without_header_rv_group, parent, false
                   )
           )
       } else{
           return ParentViewHolder(
                   LayoutInflater.from(context).inflate(
                           R.layout.category_group, parent, false
                   )
           )
       }
    }


    override fun getItemCount(): Int {
       return dataList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      val data = dataList[position]


        if (holder is ParentViewHolder){

            holder.itemTitle.text = data.headerTitle
            holder.btnMore.text = data.seeAllbtn
            var items = dataList?.get(position)?.listItem

            val itemListAdapter = ChildHorizontalAdapter(context, items)

            holder.horizontal_recycler_view.setHasFixedSize(true)
            holder.horizontal_recycler_view.layoutManager= LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.horizontal_recycler_view.adapter=itemListAdapter

            holder.horizontal_recycler_view.isNestedScrollingEnabled = false

        } else if(holder is WithoutHeaderVH){


            var items = dataList?.get(position)?.listItem

            val itemListAdapter = WithoutHeaderAdapter(context, items)

            holder.horizontal_rv_without_header.setHasFixedSize(true)
            holder.horizontal_rv_without_header.layoutManager= LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.horizontal_rv_without_header.adapter=itemListAdapter

            holder.horizontal_rv_without_header.isNestedScrollingEnabled = false

        }

    }

    override fun getItemViewType(position: Int): Int {
        return dataList.get(position).viewType?:1
    }

    companion object{
        const val VIEW_GROUP_LAYOUT = 1
        const val VIEW_WITHOUT_HEADER=2
    }

//    fun filterList(filterdNames: ArrayList<ItemGroup>) {
//        this.dataList = filterdNames
//        notifyDataSetChanged()
//    }
}