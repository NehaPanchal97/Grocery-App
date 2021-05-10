package com.grocery.app.homePage.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.constant.HomeCarousel
import com.grocery.app.databinding.CategoryGroupWithHeaderBinding
import com.grocery.app.databinding.WithoutHeaderRvGroupBinding
import com.grocery.app.homePage.dataModel.ItemGroup
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.viewHolders.BaseVH
import com.grocery.app.viewHolders.ParentViewHolder


//Main vertical Adapter for 1 screen
class HomePageCategoryAdapter(private var dataList: ArrayList<ItemGroup>?) :
    RecyclerView.Adapter<BaseVH<*, ItemGroup>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*, ItemGroup> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_GROUP_LAYOUT -> {
                val binding = CategoryGroupWithHeaderBinding.inflate(inflater, parent, false)
                ParentViewHolder(binding)
            }
            VIEW_WITHOUT_HEADER -> {
                val binding = WithoutHeaderRvGroupBinding.inflate(inflater, parent, false)
                WithoutHeaderVH(binding)
            }

            else -> {
                val binding = CategoryGroupWithHeaderBinding.inflate(inflater, parent, false)
                ParentViewHolder(binding)
            }
        }
    }


    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }


    override fun getItemViewType(position: Int): Int {
        val viewType = dataList?.getOrNull(position)?.carousel
        return if (viewType == HomeCarousel.CATEGORY) {
            VIEW_GROUP_LAYOUT
        } else {
            VIEW_WITHOUT_HEADER
        }
    }

    companion object {
        const val VIEW_GROUP_LAYOUT = 1
        const val VIEW_WITHOUT_HEADER = 2
    }


    class WithoutHeaderVH(private val binder: WithoutHeaderRvGroupBinding) :
        BaseVH<WithoutHeaderRvGroupBinding, ItemGroup>(binder) {
        //horizontal recycler view without header of homepage i.e 2 row

        override fun bind(data: ItemGroup) {
            val itemListAdapter = WithoutHeaderAdapter(data.listItem)

            binder.withoutHeaderRv.setHasFixedSize(true)
            binder.withoutHeaderRv.layoutManager =
                LinearLayoutManager(binder.root.context, LinearLayoutManager.HORIZONTAL, false)
            binder.withoutHeaderRv.adapter = itemListAdapter

            binder.withoutHeaderRv.isNestedScrollingEnabled = false
        }

    }



    override fun onBindViewHolder(holder: BaseVH<*, ItemGroup>, position: Int) {
        dataList?.getOrNull(position)?.let {
            holder.bind(it)
        }
    }

    fun update(data: ArrayList<ItemGroup>?) {
        dataList = data
        notifyDataSetChanged()
    }

}