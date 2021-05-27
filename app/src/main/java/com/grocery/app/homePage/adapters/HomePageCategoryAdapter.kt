package com.grocery.app.homePage.adapters

import android.util.Log
import android.view.LayoutInflater
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
import com.grocery.app.viewHolders.OfferPageVH
import com.grocery.app.viewHolders.ParentViewHolder
import com.grocery.app.viewHolders.WithoutHeaderGroupVH


//Main vertical Adapter for 1 screen
class HomePageCategoryAdapter(private var dataList: ArrayList<ItemGroup>?) :
    RecyclerView.Adapter<BaseVH<*, ItemGroup>>() {

    var itemClickListener: OnCategoryClickListener? = null
    var onCardClickListener:OnItemClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<*, ItemGroup> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_GROUP_LAYOUT -> {
                val binding = CategoryGroupWithHeaderBinding.inflate(inflater, parent, false)
                ParentViewHolder(binding,itemClickListener)
            }
            VIEW_WITHOUT_HEADER -> {
                val binding = WithoutHeaderRvGroupBinding.inflate(inflater, parent, false)
                WithoutHeaderGroupVH(binding,itemClickListener)
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