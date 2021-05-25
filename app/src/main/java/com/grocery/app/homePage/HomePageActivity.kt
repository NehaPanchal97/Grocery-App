package com.grocery.app.homePage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.grocery.app.R
import com.grocery.app.activities.AdminHomePageActivity
import com.grocery.app.activities.DiscountPageActivity
import com.grocery.app.activities.UpdateProfileActivity
import com.grocery.app.constant.*
import com.grocery.app.databinding.ActivityHomeBinding
import com.grocery.app.extensions.cast
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.fragments.OrderFragment
import com.grocery.app.models.Cart
import com.grocery.app.models.User
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.AuthViewModel
import com.grocery.app.viewModels.ProductViewModel
import kotlinx.android.synthetic.main.bottom_navigation_bar.*


class HomePageActivity : AppCompatActivity() {

    private val prefManager by lazy { PrefManager.getInstance(this) }
    private lateinit var viewModel: AuthViewModel
    private lateinit var productViewModel :ProductViewModel
    lateinit var binder :ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_home)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        observeData()
        val user = prefManager.get<User>(USER)
        viewModel.syncUser()
        user?.let {
            if (savedInstanceState == null) {
                switchFragment()
            }
        } ?: kotlin.run { viewModel.fetchUserInfo() }

        bottomMenuAction()
        fabAction()
        initCart()
        fabCount()
        productViewModel.fetchProductList()

    }


    private val receiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: ""
            if (action == CART_CHANGE) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    resetCart()

                } else{
                    productViewModel.cartUpdated = true
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (productViewModel.cartUpdated){
            productViewModel.cartUpdated = false
            resetCart()
        }
    }

    private fun resetCart(){
        val cart = prefManager.get(CART)?:Cart()
        productViewModel.initCartWith(cart)
        fabCount()
    }

    private fun initCart(){
       productViewModel.cart = prefManager.get(CART) ?: Cart()
        productViewModel.initCart()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(applicationContext)
                .unregisterReceiver(receiver)
    }
    private fun switchFragment(fragment: Fragment = HomeFragment()) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    private fun observeData() {
        val filter = IntentFilter()
        filter.addAction(CART_CHANGE)
        LocalBroadcastManager.getInstance(applicationContext)
                .registerReceiver(receiver,filter)

        viewModel.fetchUserLiveData.observe(this, Observer { it ->
            when (it.type) {
                Result.Status.LOADING -> {

                }
                Result.Status.SUCCESS -> {
                    currentFocus?.showSuccess("updated")
                    it.data?.let {
                        prefManager.put(USER, it)
                        if (it.role == Store.ADMIN_ROLE) {
                            startActivity(Intent(this, AdminHomePageActivity::class.java))
                            finishAffinity()
                        } else {
                            switchFragment()
                        }

                    } ?: kotlin.run {
                        startActivity(Intent(this, UpdateProfileActivity::class.java))
                        finish()
                    }
                }
                Result.Status.ERROR -> {
                    currentFocus?.showError("error")
                }
            }
        })
        viewModel.syncLiveData.observe(this, Observer { observer ->
            if (observer.type == Result.Status.SUCCESS) {
                observer.data?.let {
                    prefManager.put(it.first, it.second)
                    if (it.first == USER) {
                        val user = it.second.cast<User>()
                        if (user?.role == Store.ADMIN_ROLE) {
                            startActivity(Intent(this, AdminHomePageActivity::class.java))
                            finishAffinity()
                        }
                    }
                }
            }
        })
    }

    fun onClickBtnMore(v: View?) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, CategoryTypesFragment())
            .addToBackStack(null)
            .commit()
    }


    fun backPress(v: View?) {
        onBackPressed()
    }

    private fun fabAction(){
        fab.setOnClickListener{
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, CartPageFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

    private fun bottomMenuAction(){

        binder.navBar.navHome.setOnClickListener {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, HomeFragment())
                    .addToBackStack(null)
                    .commit()
        }

       binder.navBar.navOrder.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, OrderFragment())
                .addToBackStack(null)
                .commit()
        }

        binder.navBar.navOffer.setOnClickListener {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, OfferFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

    private fun fabCount(){
        val cartCount =productViewModel.cart.items?.size?:0
        val count = binder.navBar.cartBadge
        count.text = cartCount.toString()
        if (cartCount == 0) {
           binder.navBar.cartBadge.visible(false)
        } else {
            binder.navBar.cartBadge.visible(true)
        }

    }

}

