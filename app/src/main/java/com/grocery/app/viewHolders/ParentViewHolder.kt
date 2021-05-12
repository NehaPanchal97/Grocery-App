package com.grocery.app.viewHolders

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.grocery.app.R
import com.grocery.app.databinding.CategoryGroupWithHeaderBinding
import com.grocery.app.homePage.CategoryTypesFragment
import com.grocery.app.homePage.ProductListFragment
import com.grocery.app.homePage.adapters.CategoryTypesAdapter
import com.grocery.app.homePage.dataModel.ItemGroup
import com.grocery.app.listeners.OnCategoryClickListener
import com.grocery.app.listeners.OnItemClickListener

class ParentViewHolder(private val binder: CategoryGroupWithHeaderBinding, private val itemCategoryClickListener: OnCategoryClickListener?) :
        BaseVH<CategoryGroupWithHeaderBinding, ItemGroup>(binder) {


    override fun bind(data: ItemGroup) {

        val itemListAdapter = data.listItem?.let { CategoryTypesAdapter(it).apply {
            itemClickListener = itemCategoryClickListener
        } }

        binder.recyclerViewHorizontal.setHasFixedSize(true)
        binder.recyclerViewHorizontal.layoutManager =
                LinearLayoutManager(binder.root.context, LinearLayoutManager.HORIZONTAL, false)
        binder.recyclerViewHorizontal.adapter = itemListAdapter

        binder.recyclerViewHorizontal.isNestedScrollingEnabled = false
    }


}