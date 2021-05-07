package com.grocery.app.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.constant.CATEGORY
import com.grocery.app.databinding.ProductItemsGroupBinding
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.homePage.adapters.CategoryItemsAdapter
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.viewModels.CategoryViewModel

class CategoryTypesFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binder: ProductItemsGroupBinding
    lateinit var listAdapter: CategoryItemsAdapter
    lateinit var viewModel: CategoryViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)
        setupView()
        observe()
        viewModel.fetchCategoryList()
    }

    private fun observe() {
        viewModel.catListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    binder.catProgressBar.show()
                }
                Result.Status.SUCCESS -> {
                    binder.catProgressBar.hide()
                    listAdapter.updateCategory(it.data ?: arrayListOf())
                }
                else -> {
                    binder.catProgressBar.hide()
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
        binder = ProductItemsGroupBinding.inflate(inflater, container, false)
        return binder.root

    }


    private fun setupView() {


        binder.productRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            listAdapter = CategoryItemsAdapter(arrayListOf())
                .apply { itemClickListener = _itemClickListener }
            binder.productRecyclerView.adapter = listAdapter

        }
        binder.backBtn.setOnClickListener(this)
    }


    private val _itemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {

            val bundle = Bundle()
            bundle.putParcelable(CATEGORY, listAdapter.items[position])
            val fragment = CategoryItemsFragment()
            fragment.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }

    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.back_btn) {
            activity?.onBackPressed()
        }
    }


}