package com.grocery.app.homePage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.activities.DiscountPageActivity
import com.grocery.app.constant.DISCOUNT
import com.grocery.app.constant.OFFER_PAGE
import com.grocery.app.constant.ORDER_ID
import com.grocery.app.constant.TITLE
import com.grocery.app.databinding.OfferFragmentBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.homePage.adapters.WithoutHeaderAdapter
import com.grocery.app.listeners.OnCategoryClickListener
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Category
import com.grocery.app.viewModels.CategoryViewModel
import com.grocery.app.viewModels.ProductViewModel
import kotlinx.android.synthetic.main.offer_page_item.*

class OfferFragment: BaseFragment(), OnCategoryClickListener {

    lateinit var  binder : OfferFragmentBinding
    lateinit var listAdapter: WithoutHeaderAdapter
    lateinit var viewModel:CategoryViewModel
    lateinit var productViewModel:ProductViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        productViewModel=ViewModelProvider(this).get(ProductViewModel::class.java)
        binder.offerRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        setUpView()
        observe()
        viewModel.fetchOfferDetail()

    }


    private fun observe(){
        viewModel.offerPageLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {

                }

                Result.Status.SUCCESS->{
                    val products = it.data ?: arrayListOf()
                    listAdapter.update(products)
                }

                Result.Status.ERROR->{
                    binder.root.showError("unable to fetch")
                }
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binder = OfferFragmentBinding.inflate(inflater, container, false)

        return binder.root
    }

    private fun setUpView() {
        binder.offerRecyclerView.apply {
            listAdapter = WithoutHeaderAdapter(arrayListOf(), OFFER_PAGE)
            binder.offerRecyclerView.adapter = listAdapter

        }
        listAdapter.itemClickListener=this
    }


    override fun onItemClick(itemId: Int, category: Category) {
        val discount = category.discount
        val title = category.offerTitle
        val intent = DiscountPageActivity.newIntent(requireContext(),discount?:0.0,title?:"")
        startActivity(intent)

    }


}