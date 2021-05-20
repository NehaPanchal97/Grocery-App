package com.grocery.app.homePage

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
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
import org.w3c.dom.Text


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
        fabCount()
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

//        productViewModel.fetchProductList()
    }


    private fun switchFragment(fragment: Fragment = HomeFragment()) {
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

        binder.navBar.llHome.setOnClickListener {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, HomeFragment())
                    .addToBackStack(null)
                    .commit()
        }

       binder.navBar.llOrder.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, OrderFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun fabCount(){
        val cartCount = 2
        val count = findViewById<TextView>(R.id.cart_badge)
        count.text = cartCount.toString()
        if (cartCount == 0) {
            if (count?.visibility != View.GONE) {
                count?.visibility = View.GONE
            }
        } else {
            if (count?.visibility != View.VISIBLE) {
                count?.visibility = View.VISIBLE
            }
        }

    }

}

