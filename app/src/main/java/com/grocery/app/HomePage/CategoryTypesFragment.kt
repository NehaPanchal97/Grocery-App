package com.grocery.app.HomePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.HomePage.Adapters.ProductItemsAdapter
import com.grocery.app.HomePage.DataModel.ItemData
import com.grocery.app.R
import kotlinx.android.synthetic.main.product_items_group.*

class CategoryTypesFragment : Fragment() {

    lateinit var productRecyclerViewAdapter: ProductItemsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        product_recyclerView.layoutManager = GridLayoutManager(context,3,RecyclerView.VERTICAL,false)
        startRecyclerView()

    }




    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.product_items_group, container, false)

    }


    private fun startRecyclerView() {
        product_recyclerView.apply {
            val itemdata = ArrayList<ItemData>()
            val item_1 = ItemData()
            val item_2 = ItemData()
            val item_3 = ItemData()
            val item_4 = ItemData()
            val item_5 = ItemData()
            val item_6 = ItemData()
            val item_7 = ItemData()
            val item_8 = ItemData()
            val item_9 = ItemData()
            val item_10=ItemData()
            val item_11=ItemData()
            val item_12=ItemData()
            item_1.name = "Strawberry"
            item_1.image = "https://app.berrika.com/admin/template/assets/images/products/2020-09-0817:22:45-13_Strawberries.png"
            item_2.name = "Orange"
            item_2.image = "https://i.pinimg.com/originals/12/4e/52/124e5280ba9b02ed3db753542a5fe68c.png"
            item_3.name = "Orange"
            item_3.image = "https://i.pinimg.com/originals/12/4e/52/124e5280ba9b02ed3db753542a5fe68c.png"
            item_4.name = "Guava"
            item_4.image = "https://www.acai.eu/acai/CustomUpload/374O357O340O370O356O369O350O320O322O320O328O/Guave-Pueree-FineFruitsClub-500x500px.gif"
            item_5.name = "Banana"
            item_5.image = "https://www.sahyadrifarms.com/images/prominent-crops/bananas.png"
            item_6.name = "Mango"
            item_6.image = "https://pisumfoods.com/img/product/fruits/mango.jpg"
            item_7.name = "Apple"
            item_7.image = "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/sugar-comparing-apple-1534800501.png?crop=0.839xw:0.839xh;0.0777xw,0.159xh&resize=480:*"
            item_8.name = "Mango"
            item_8.image = "https://pisumfoods.com/img/product/fruits/mango.jpg"
            item_9.name = "Milk & Egg"
            item_9.image = "https://www.seekpng.com/png/full/27-271499_hormone-free-milk-milk-and-eggs-png.png"
            item_10.name = "Meat"
            item_10.image="https://5.imimg.com/data5/SELLER/Default/2020/10/QC/SH/YH/110830185/fresh-meat-250x250.png"
            item_11.name="Fish"
            item_11.image="https://cdn.shinjukuhalalfood.com/wp-content/uploads/2020/11/13041118/ayer-fish.png"
            item_12.name="Cake"
            item_12.image="https://cakeowls.com/wp-content/uploads/2020/04/IMG-20200102-WA0048.png"

            itemdata.add(item_1)
            itemdata.add(item_9)
            itemdata.add(item_10)
            itemdata.add(item_11)
            itemdata.add(item_12)
            itemdata.add(item_6)
            itemdata.add(item_7)
            itemdata.add(item_8)
            itemdata.add(item_9)
            itemdata.add(item_2)
            itemdata.add(item_3)
            itemdata.add(item_4)
            itemdata.add(item_4)
            itemdata.add(item_4)
            itemdata.add(item_4)
            itemdata.add(item_4)


            productRecyclerViewAdapter= ProductItemsAdapter(context, itemdata)
            adapter=productRecyclerViewAdapter
        }
    }


}