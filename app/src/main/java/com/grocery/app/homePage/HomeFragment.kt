package com.grocery.app.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.constant.CATEGORY
import com.grocery.app.contracts.UpdateProfileContract
import com.grocery.app.homePage.adapters.HomePageCategoryAdapter
import com.grocery.app.databinding.HomeFragmentBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.viewModels.CategoryViewModel
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : BaseFragment() {

    private lateinit var binder: HomeFragmentBinding
    lateinit var listAdapter: HomePageCategoryAdapter
    private lateinit var viewModel: CategoryViewModel
     var imgBtn: ImageView?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        catRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        imgBtn= view.findViewById(R.id.homeItemImage)
        imgBtn?.setOnClickListener (object:View.OnClickListener {
            override fun onClick(v: View?) {
                activity?.supportFragmentManager?.beginTransaction()
                        ?.add(R.id.fragment_container, ProductListFragment())
                        ?.addToBackStack(null)
                        ?.commit()
            }

        })

        setupView()
        observe()
        viewModel.fetchHomePageData(3)
    }

    private fun observe() {
        viewModel.homePageLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    binder.homeProgressBar.show()
                }
                Result.Status.SUCCESS -> {
                    binder.homeProgressBar.hide()
                    listAdapter.update(it.data )
                }
                else -> {
                    home_progress_bar.hide()
                    binder.root.showError("Unable to fetch categories")
                }
            }
        })

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binder = HomeFragmentBinding.inflate(inflater, container, false)
        return binder.root
    }

    private val _updateProfileCallback =
        registerForActivityResult(UpdateProfileContract()) { result ->
            if (result) {
                //show Toast like profile updated or something like that
            }
        }

    private fun setupView() {
        binder.ivAccountDetails.setOnClickListener {
            _updateProfileCallback.launch(null)
        }
        catRecyclerView.apply {
            listAdapter = HomePageCategoryAdapter(arrayListOf())
            binder.catRecyclerView.adapter = listAdapter
        }
    }


}