package com.grocery.app.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.homePage.adapters.HomePageCategoryAdapter
import com.grocery.app.R
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.viewModels.CategoryViewModel
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : BaseFragment() {

    lateinit var lisAdapter: HomePageCategoryAdapter
    private lateinit var viewModel: CategoryViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        initRecyclerView()
        observe()
        viewModel.fetchCategoryList(3)
    }

    private fun observe() {
        viewModel.catListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    // TODO: 3/5/21 show Loader
                }
                Result.Status.SUCCESS -> {
                    // TODO: 3/5/21 Hide loader
                    lisAdapter.updateCategory(it.data)
                }
                Result.Status.ERROR -> {
                    // TODO: 3/5/21 hide loader
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
            lisAdapter = HomePageCategoryAdapter(arrayListOf())
            adapter = lisAdapter
        }
    }


}