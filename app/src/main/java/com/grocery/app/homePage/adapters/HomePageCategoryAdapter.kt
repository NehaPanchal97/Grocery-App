package com.grocery.app.homePage.adapters

import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.activities.DetailsPageActivity
import com.grocery.app.activities.DiscountPageActivity
import com.grocery.app.constant.HomeCarousel
import com.grocery.app.constant.WITHOUT_HEADER_HOME_PAGE
import com.grocery.app.databinding.CategoryGroupWithHeaderBinding
import com.grocery.app.databinding.WithoutHeaderRvGroupBinding
import com.grocery.app.fragments.OrderFragment
import com.grocery.app.homePage.dataModel.ItemGroup
import com.grocery.app.listeners.OnCategoryClickListener
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Category
import com.grocery.app.viewHolders.BaseVH
import com.grocery.app.viewHolders.ParentViewHolder


//Main vertical Adapter for 1 screen
class HomePageCategoryAdapter(private var dataList: ArrayList<ItemGroup>?) :
    RecyclerView.Adapter<BaseVH<*, ItemGroup>>() {

    var itemClickListener: OnCategoryClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*, ItemGroup> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_GROUP_LAYOUT -> {
                val binding = CategoryGroupWithHeaderBinding.inflate(inflater, parent, false)
                ParentViewHolder(binding,itemClickListener)
            }
            VIEW_WITHOUT_HEADER -> {
                val binding = WithoutHeaderRvGroupBinding.inflate(inflater, parent, false)
                WithoutHeaderVH(binding)
            }

            else -> {
                val binding = CategoryGroupWithHeaderBinding.inflate(inflater, parent, false)
                ParentViewHolder(binding,itemClickListener)
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
        BaseVH<WithoutHeaderRvGroupBinding, ItemGroup>(binder), OnItemClickListener {
        //horizontal recycler view without header of homepage i.e 2 row

        override fun bind(data: ItemGroup) {
            val itemListAdapter = data.listItem?.let { WithoutHeaderAdapter(ArrayList(it), WITHOUT_HEADER_HOME_PAGE) }

            binder.withoutHeaderRv.setHasFixedSize(true)
            binder.withoutHeaderRv.layoutManager =
                LinearLayoutManager(binder.root.context, LinearLayoutManager.HORIZONTAL, false)
            binder.withoutHeaderRv.adapter = itemListAdapter

            binder.withoutHeaderRv.isNestedScrollingEnabled = false

            itemListAdapter?.itemClickListener=this
        }

        override fun onItemClick(itemId: Int, position: Int) {
           Log.d("pressed","Btn Clicked")

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