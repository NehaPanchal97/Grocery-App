package com.grocery.app.homePage

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.grocery.app.R
import com.grocery.app.activities.AdminHomePageActivity
import com.grocery.app.activities.UpdateProfileActivity
import com.grocery.app.constant.Store
import com.grocery.app.constant.USER
import com.grocery.app.databinding.ActivityHomeBinding
import com.grocery.app.extensions.cast
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
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

        bottomMenuAction()
        fabAction()
        cartCount()
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        observeData()
//       switchFragment()
        val user = prefManager.get<User>(USER)
        viewModel.syncUser()
        user?.let {
            if (savedInstanceState == null) {
                switchFragment()
            }
        } ?: kotlin.run { viewModel.fetchUserInfo() }

        productViewModel.fetchProductList()
    }


    private fun switchFragment(fragment: Fragment = HomeFragment()) {
//        bottomNavigationBar.background = null
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    private fun observeData() {
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
        ll_home.setOnClickListener {
            startActivity(Intent(this, this::class.java))
        }

        ll_order.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, OrderFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun cartCount(){
        fab.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("UnsafeExperimentalUsageError")
            override fun onGlobalLayout() {
                val count = productViewModel.cartMap.size
                val badgeDrawable = BadgeDrawable.create(this@HomePageActivity)
                badgeDrawable.number = count
                badgeDrawable.backgroundColor = Color.BLUE
                badgeDrawable.horizontalOffset =30
                badgeDrawable.verticalOffset =20
                BadgeUtils.attachBadgeDrawable(badgeDrawable,fab,null)
                fab.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

}

