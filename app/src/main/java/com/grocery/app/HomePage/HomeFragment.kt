package com.grocery.app.HomePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.HomePage.Adapters.MainAdapterVertical
import com.grocery.app.HomePage.DataModel.ItemData
import com.grocery.app.HomePage.DataModel.ItemGroup
import com.grocery.app.R
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : Fragment() {

    lateinit var recyclerViewAdapter: MainAdapterVertical
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        bottomNavigationBar.background=null
//        bottomNavigationBar.menu.getItem(2).isEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        initRecyclerView()
    }



    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false)

    }

    private fun initRecyclerView() {
        recyclerView.apply {
            val list = ArrayList<ItemGroup>()
            val viewGroup = ItemGroup()
            val viewGroup1 = ItemGroup()
            val viewGroup2 = ItemGroup()
            viewGroup.headerTitle = "Categories"
            viewGroup.seeAllbtn = "See All"
            viewGroup2.headerTitle = "Popular Deals"
            viewGroup2.seeAllbtn = "See All"
            viewGroup.viewType = 1
            viewGroup1.viewType = 2
            val listData= ArrayList<ItemData>()
            val d1= ItemData()
            val d2 = ItemData()
            val d3 = ItemData()
            d1.name = "vegetables"
            d1.image = "https://i.pinimg.com/originals/a5/f3/5f/a5f35fb23e942809da3df91b23718e8d.png"
            d2.name="fruits"
            d2.image ="https://i.pinimg.com/originals/12/4e/52/124e5280ba9b02ed3db753542a5fe68c.png"
            d3.name = "fruits"
            d3.image = "https://app.berrika.com/admin/template/assets/images/products/2020-09-0817:22:45-13_Strawberries.png"
            listData.add(d1)
            listData.add(d2)
            listData.add(d3)
            listData.add(d1)
            viewGroup.listItem = listData
            viewGroup1.listItem = listData
            viewGroup2.listItem = listData
            list.add(viewGroup)
            list.add(viewGroup1)
            list.add(viewGroup2)
            recyclerViewAdapter= MainAdapterVertical(context, list)
            adapter=recyclerViewAdapter
        }
    }


}