package com.grocery.app.activities

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.grocery.app.R
import com.grocery.app.customs.PageFactory
import com.grocery.app.databinding.ActivityAdminHomePageBinding
import com.grocery.app.extensions.cast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class AdminHomePageActivity : BaseActivity() {

    private lateinit var binder: ActivityAdminHomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_admin_home_page)
        setupView()
    }

    private fun setupView() {
        switchPage(R.id.order)

        val navBar = findViewById<BottomNavigationView>(R.id.nav_bar)
        navBar.setOnNavigationItemSelectedListener(_navItemSelectedListener)
        binder.toolBar.setOnMenuItemClickListener(_menuItemClickListener)

        val searchView = binder.toolBar.menu
            .findItem(R.id.app_bar_search)
            .actionView?.cast<SearchView>()


        searchView?.setOnQueryTextListener(searchQueryChangeListener)
//        val searchIcon = searchView?.getChildAt(0)
//            ?.cast<ViewGroup>()?.getChildAt(1)
//            ?.cast<ImageView>()

//        searchIcon?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_white_search))

    }

    private val currentFragment
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container)

    private val searchQueryChangeListener = object : SearchView.OnQueryTextListener {


        override fun onQueryTextSubmit(query: String?): Boolean {
            return currentFragment?.cast<SearchView.OnQueryTextListener>()
                ?.onQueryTextSubmit(query) ?: false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return currentFragment?.cast<SearchView.OnQueryTextListener>()
                ?.onQueryTextChange(newText) ?: false
        }

    }

    private val _menuItemClickListener = Toolbar.OnMenuItemClickListener {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        val flag = fragment?.cast<Toolbar.OnMenuItemClickListener>()
            ?.onMenuItemClick(it) ?: false
        flag
    }

    private val _navItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            switchPage(item.itemId)
            true
        }

    private fun switchPage(itemId: Int) {
        val page = PageFactory.create(menuId = itemId)
        binder.toolBar.title = page.getToolbar(this)
        page.setupToolbar(binder.toolBar)
        switchFragment(page.getFragment())
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}