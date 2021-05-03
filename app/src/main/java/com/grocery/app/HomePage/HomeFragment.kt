package com.grocery.app.HomePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.HomePage.Adapters.HomePageCategoryAdapter
import com.grocery.app.HomePage.DataModel.ItemData
import com.grocery.app.HomePage.DataModel.ItemGroup
import com.grocery.app.R
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.viewModels.CategoryViewModel
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment :BaseFragment() {

    lateinit var recyclerViewAdapter: HomePageCategoryAdapter
    private lateinit var viewModel:CategoryViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        initRecyclerView()
        observe()
        viewModel.fetchCategoryList()
    }

    private fun observe() {
        viewModel.catListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type){
                Result.Status.LOADING->{
                    binder.progressBar.show()
                }
                Result.Status.SUCCESS->{
                    binder.progressBar.hide()

                }
            }
        })
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
            recyclerViewAdapter= HomePageCategoryAdapter(arrayListOf())
            adapter=recyclerViewAdapter
        }
    }


}