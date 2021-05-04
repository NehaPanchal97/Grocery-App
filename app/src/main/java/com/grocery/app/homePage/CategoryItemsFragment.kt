package com.grocery.app.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.homePage.adapters.SpecificItemAdapter
import com.grocery.app.homePage.dataModel.ItemData
import com.grocery.app.R
import com.grocery.app.fragments.BaseFragment
import kotlinx.android.synthetic.main.specific_itemgroup_in_product.*

class CategoryItemsFragment : BaseFragment() {

    lateinit var itemRecyclerViewAdapter: SpecificItemAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        item_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))
        item_recyclerView.layoutManager = GridLayoutManager(context,2, RecyclerView.VERTICAL,false)
        itemRecyclerView()

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.specific_itemgroup_in_product, container, false)

    }

    private fun itemRecyclerView() {
        item_recyclerView.apply {
            val itemData= ArrayList<ItemData>()
            val d1 = ItemData()
            val d2 = ItemData()
            val d3 = ItemData()
            val d4 = ItemData()
            val d5 = ItemData()
            d1.name = "Strawberry"
            d1.image = "https://app.berrika.com/admin/template/assets/images/products/2020-09-0817:22:45-13_Strawberries.png"
            d1.amount="$15.00/Kg"
            d1.price="$15.20"
            d2.name = "Orange"
            d2.image = "https://i.pinimg.com/originals/12/4e/52/124e5280ba9b02ed3db753542a5fe68c.png"
            d2.amount="$15.00/Kg"
            d2.price="$15.20"
            d3.name = "Mango"
            d3.image = "https://pisumfoods.com/img/product/fruits/mango.jpg"
            d3.amount="$15.00/Kg"
            d3.price="$15.20"
            d4.name = "Apple"
            d4.image = "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/sugar-comparing-apple-1534800501.png?crop=0.839xw:0.839xh;0.0777xw,0.159xh&resize=480:*"
            d4.amount="$15.00/Kg"
            d4.price="$15.20"
            d5.name = "Guava"
            d5.image = "https://www.acai.eu/acai/CustomUpload/374O357O340O370O356O369O350O320O322O320O328O/Guave-Pueree-FineFruitsClub-500x500px.gif"
            d5.amount="$15.00/Kg"
            d5.price="$15.20"

            itemData.add(d1)
            itemData.add(d2)
            itemData.add(d3)
            itemData.add(d4)
            itemData.add(d5)
            itemData.add(d4)
            itemData.add(d3)
            itemData.add(d2)
            itemData.add(d1)

            itemRecyclerViewAdapter = SpecificItemAdapter(context,itemData)
            adapter=itemRecyclerViewAdapter
        }
    }

}