package com.grocery.app.homePage.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.homePage.dataModel.ItemGroup
import com.grocery.app.databinding.CategoryGroupBinding
import com.grocery.app.databinding.WithoutHeaderRvGroupBinding
import com.grocery.app.models.Category
import com.grocery.app.viewHolders.BaseVH
import kotlin.collections.ArrayList


//Main vertical Adapter for 1 screen
class HomePageCategoryAdapter(private var dataList: ArrayList<ItemGroup>) :
    RecyclerView.Adapter<BaseVH<*, ItemGroup>>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*, ItemGroup> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_GROUP_LAYOUT -> {
                val binding = CategoryGroupBinding.inflate(inflater, parent, false)
                ParentViewHolder(binding)
            }
            VIEW_WITHOUT_HEADER -> {
                val binding = WithoutHeaderRvGroupBinding.inflate(inflater, parent, false)
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
        return VIEW_GROUP_LAYOUT
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

    class ParentViewHolder(private val binder: CategoryGroupBinding) :
        BaseVH<CategoryGroupBinding, ItemGroup>(binder) {
        override fun bind(data: ItemGroup) {

            val itemListAdapter = ProductListAdapter(data.listItem)

            binder.recyclerViewHorizontal.setHasFixedSize(true)
            binder.recyclerViewHorizontal.layoutManager =
                LinearLayoutManager(binder.root.context, LinearLayoutManager.HORIZONTAL, false)
            binder.recyclerViewHorizontal.adapter = itemListAdapter

            binder.recyclerViewHorizontal.isNestedScrollingEnabled = false
        }

    }

    override fun onBindViewHolder(holder: BaseVH<*, ItemGroup>, position: Int) {
        holder.bind(dataList[position])
    }

    fun updateCategory(data: ArrayList<Category>?) {
        val categories = data ?: arrayListOf()
        if (dataList.isEmpty()) {
            dataList.add(ItemGroup(listItem = categories))
            notifyDataSetChanged()
        } else {
            val itemGroup = dataList[0]
            itemGroup.listItem = categories
            notifyItemChanged(0)
        }

    }

}