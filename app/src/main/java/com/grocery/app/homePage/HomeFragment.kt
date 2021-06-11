package com.grocery.app.homePage

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.activities.DiscountPageActivity
import com.grocery.app.activities.SearchActivity
import com.grocery.app.constant.CATEGORY
import com.grocery.app.constant.UPDATE_PROFILE
import com.grocery.app.constant.USER
import com.grocery.app.contracts.UpdateProfileContract
import com.grocery.app.databinding.HomeFragmentBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.showError
import com.grocery.app.extras.Result
import com.grocery.app.fragments.BaseFragment
import com.grocery.app.homePage.adapters.HomePageCategoryAdapter
import com.grocery.app.listeners.OnCategoryClickListener
import com.grocery.app.models.Category
import com.grocery.app.models.User
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.AuthViewModel
import com.grocery.app.viewModels.CategoryViewModel
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : BaseFragment() {
    private val prefManager by lazy { PrefManager.getInstance(requireContext()) }
    private lateinit var binder: HomeFragmentBinding
    lateinit var listAdapter: HomePageCategoryAdapter
    private lateinit var viewModel: CategoryViewModel
    private lateinit var authViewModel : AuthViewModel
    companion object{
        const val CART_ICON_REQUEST_CODE = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        catRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        loadProfile()
        setupView()
        observe()
        viewModel.fetchHomePageData(6)

        binder.searchContainer.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivityForResult(intent, CART_ICON_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CART_ICON_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                (activity as HomePageActivity).cartIconAction()
            }
        }
    }

    private fun loadProfile() {
    val user = prefManager.get<User>(USER)
        val userName = user?.name?.capitalize()
        binder.tvText.text =getString(R.string.welcome_text_header,userName)
        binder.ivAccountDetails.loadImage(user?.url,R.drawable.ic_circle_account,true)
    }

    private val _receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: ""
            if(action == UPDATE_PROFILE){
                if (lifecycle.currentState == Lifecycle.State.RESUMED){
                    loadProfile()
                }
                else{
                    authViewModel.profileUpdated = true
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (authViewModel.profileUpdated){
            authViewModel.profileUpdated = false
            loadProfile()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(_receiver)
    }

    private fun observe() {
        val filter = IntentFilter()
        filter.addAction(UPDATE_PROFILE)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(_receiver, filter)
        viewModel.homePageLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    binder.homeProgressBar.show()
                }
                Result.Status.SUCCESS -> {
                    binder.homeProgressBar.hide()
                    listAdapter.update(it.data)

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
                .apply { itemClickListener = _itemClickListener }
            binder.catRecyclerView.adapter = listAdapter

        }

    }


    private val _itemClickListener = object : OnCategoryClickListener {
        override fun onItemClick(itemId: Int, category: Category) {
            if (category.offerTitle.isNullOrEmpty()) {
                val bundle = Bundle()
                bundle.putParcelable(CATEGORY, category)
                val fragment = ProductListFragment()
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.fragment_container, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
            } else {

                val discount = category.discount
                val title = category.offerTitle
                val intent = DiscountPageActivity.newIntent(
                    requireContext(), discount
                        ?: 0.0, title ?: ""
                )
                startActivity(intent)
            }
        }
    }


}