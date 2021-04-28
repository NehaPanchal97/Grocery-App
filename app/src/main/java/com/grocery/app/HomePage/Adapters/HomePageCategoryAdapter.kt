package com.grocery.app.HomePage.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.HomePage.DataModel.ItemGroup
import com.grocery.app.R
import com.grocery.app.databinding.CategoryGroupBinding
import com.grocery.app.databinding.WithoutHeaderRvGroupBinding
import com.grocery.app.viewHolders.BaseVH


//Main vertical Adapter for 1 screen
class HomePageCategoryAdapter(private val context: Context,
                              private var dataList: ArrayList<ItemGroup>
                         ):
    RecyclerView.Adapter<BaseVH<*,ItemGroup>> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*, ItemGroup> {
        val inflater = LayoutInflater.from(parent.context)
        return  when(viewType){
            VIEW_GROUP_LAYOUT ->{
                val binding = CategoryGroupBinding.inflate(inflater, parent, false)
                ParentViewHolder(binding)
            }
            VIEW_WITHOUT_HEADER->{
                val binding = WithoutHeaderRvGroupBinding.inflate(inflater,parent,false)
                WithoutHeaderVH(binding)
            }

            else -> {
                val binding = CategoryGroupBinding.inflate(inflater, parent, false)
                ParentViewHolder(binding)
            }
        }
    }


    override fun getItemCount(): Int {
       return dataList.size
    }


    override fun getItemViewType(position: Int): Int {
        return dataList.get(position).viewType?:1
    }

    companion object{
        const val VIEW_GROUP_LAYOUT = 1
        const val VIEW_WITHOUT_HEADER=2
    }


    class WithoutHeaderVH(private val binder: WithoutHeaderRvGroupBinding):BaseVH<WithoutHeaderRvGroupBinding,ItemGroup>(binder){
        //horizontal recycler view without header of homepage i.e 2 row

        override fun bind(data: ItemGroup) {
            val itemListAdapter = WithoutHeaderAdapter(binder.root.context, data.listItem)

            binder.withoutHeaderRv.setHasFixedSize(true)
            binder.withoutHeaderRv.layoutManager= LinearLayoutManager(binder.root.context, LinearLayoutManager.HORIZONTAL, false)
            binder.withoutHeaderRv.adapter=itemListAdapter

            binder.withoutHeaderRv.isNestedScrollingEnabled = false
        }

    }

    class ParentViewHolder(private val binder: CategoryGroupBinding):BaseVH<CategoryGroupBinding,ItemGroup>(binder) {
        override fun bind(data: ItemGroup) {
            binder.itemTitle.text = data.headerTitle
           binder. btnMore.text = data.seeAllbtn

            val itemListAdapter = ProductListAdapter(binder.root.context, data.listItem)

           binder.recyclerViewHorizontal.setHasFixedSize(true)
            binder.recyclerViewHorizontal.layoutManager= LinearLayoutManager(binder.root.context, LinearLayoutManager.HORIZONTAL, false)
            binder.recyclerViewHorizontal.adapter=itemListAdapter

            binder.recyclerViewHorizontal.isNestedScrollingEnabled = false
        }

    }

    override fun onBindViewHolder(holder: BaseVH<*, ItemGroup>, position: Int) {
        holder.bind(dataList[position])
    }

}