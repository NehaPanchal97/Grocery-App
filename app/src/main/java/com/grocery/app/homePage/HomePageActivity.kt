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
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.grocery.app.R
import com.grocery.app.activities.AboutPageActivity
import com.grocery.app.activities.AdminHomePageActivity
import com.grocery.app.activities.UpdateProfileActivity
import com.grocery.app.constant.CART
import com.grocery.app.constant.CART_CHANGE
import com.grocery.app.constant.Store
import com.grocery.app.constant.USER
import com.grocery.app.databinding.ActivityHomeBinding
import com.grocery.app.extensions.*
import com.grocery.app.extras.Result
import com.grocery.app.fragments.OrderFragment
import com.grocery.app.models.Cart
import com.grocery.app.models.User
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.AuthViewModel
import com.grocery.app.viewModels.ProductViewModel
import kotlinx.android.synthetic.main.bottom_navigation_bar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomePageActivity : AppCompatActivity() {

    private val prefManager by lazy { PrefManager.getInstance(this) }
    private lateinit var viewModel: AuthViewModel
    private lateinit var productViewModel: ProductViewModel
    lateinit var binder: ActivityHomeBinding
    private var askToExit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_home)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        val radius = resources.getDimension(R.dimen.nav_bar_top_radius)
        val bottomAppBar = binder.navBar.bottomAppBar

        val bottomBarBackground =
            bottomAppBar.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
            .toBuilder()
            .setTopRightCorner(CornerFamily.ROUNDED, radius)
            .setTopLeftCorner(CornerFamily.ROUNDED, radius)
            .build()

        observeData()
        val user = prefManager.get<User>(USER)
        viewModel.syncUser()
        user?.let {
            if (savedInstanceState == null) {
                switchFragment()
            }
        } ?: kotlin.run { viewModel.fetchUserInfo() }
        bottomNavAction()
        fabAction()
        initCart()
        fabCount()
        productViewModel.fetchProductList()

    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: ""
            if (action == CART_CHANGE) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    resetCart()

                } else {
                    productViewModel.cartUpdated = true
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (productViewModel.cartUpdated) {
            productViewModel.cartUpdated = false
            resetCart()
        }
    }

    private fun resetCart() {
        val cart = prefManager.get(CART) ?: Cart()
        productViewModel.initCartWith(cart)
        fabCount()
    }

    private fun initCart() {
        productViewModel.cart = prefManager.get(CART) ?: Cart()
        productViewModel.initCart()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver(receiver)
    }

    private fun clearBackStack() {
        var count = supportFragmentManager.backStackEntryCount
        while (count-- > 0) {
            supportFragmentManager.popBackStack()
        }
    }

    fun switchFragment(
        fragment: Fragment = HomeFragment(),
        addToBackstack: Boolean = false,
        addFragment: Boolean = false
    ) {
        val trans = supportFragmentManager.beginTransaction()
        if (addFragment) {
            trans.add(R.id.fragment_container, fragment)
        } else {
            clearBackStack()
            trans.replace(R.id.fragment_container, fragment)
        }
        if (addToBackstack) {
            trans.addToBackStack(null)
        }
        trans.commit()
    }

    private fun observeData() {
        val filter = IntentFilter()
        filter.addAction(CART_CHANGE)
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(receiver, filter)

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

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            GlobalScope.launch(Dispatchers.Main) {
                if (askToExit) {
                    askToExit = false
                    this@HomePageActivity.showToast(getString(R.string.press_again_to_exit), 2000)
                    delay(2000)
                    askToExit = true
                } else {
                    finish()
                }
            }
        }
    }

    private fun fabAction() {
        fab.setOnClickListener {
            switchFragment(CartPageFragment(), addToBackstack = true, addFragment = true)
        }
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container)


    private fun bottomNavAction() {
        binder.navBar.bottomNavigationView.background = null
        binder.navBar.bottomNavigationView.menu.getItem(2).isEnabled = false
        binder.navBar.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    if (currentFragment !is HomeFragment) {
                        switchFragment()
                    }
                }
                R.id.order -> {
                    if (currentFragment !is OrderFragment) {
                        switchFragment(OrderFragment())
                    }
                }
                R.id.offer -> {
                    if (currentFragment !is OfferFragment) {
                        switchFragment(OfferFragment())
                    }
                }
                R.id.more -> {
                    val intent = Intent(this, AboutPageActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

    }


    private fun fabCount() {
        val cartCount = productViewModel.cart.items?.size ?: 0
        val count = binder.navBar.cartBadge
        count.text = cartCount.toString()
        binder.navBar.cartBadge.visible(cartCount > 0)


    }

}

