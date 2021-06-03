package com.grocery.app.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
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
    private var menuId: Int = R.id.order
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_admin_home_page)
        setupView()
    }

    private fun setupView() {
        switchPage(menuId)

        val navBar = findViewById<BottomNavigationView>(R.id.nav_bar)
        navBar.setOnNavigationItemSelectedListener(_navItemSelectedListener)
        binder.toolBar.setOnMenuItemClickListener(_menuItemClickListener)

        val searchView = binder.toolBar.menu
            .findItem(R.id.app_bar_search)
            .actionView?.cast<SearchView>()

        configSearchView(searchView)
    }

    private fun configSearchView(searchView: SearchView?) {
        searchView?.queryHint = getString(R.string.search_product)
        searchView?.setOnQueryTextListener(searchQueryChangeListener)


        val hintColor = ContextCompat.getColor(this, R.color.white_hint)
        val textColor = ContextCompat.getColor(this, R.color.white_color)


        val searchTextView =
            searchView?.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchTextView?.setHintTextColor(hintColor)
        searchTextView?.setTextColor(textColor)
    }

    private fun resetSearch() {
        val searchView = binder.toolBar.menu
            .findItem(R.id.app_bar_search)
            .actionView?.cast<SearchView>()
        searchView?.isIconified = true
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
            if (item.itemId != menuId) {
                switchPage(item.itemId)
            }
            true
        }

    private fun switchPage(itemId: Int) {
        menuId = itemId
        val page = PageFactory.create(menuId = itemId)
        binder.toolBar.title = page.getToolbar(this)
        page.setupToolbar(binder.toolBar)

        val fragment = page.getFragment()
        if (fragment is SearchView.OnQueryTextListener) {
            resetSearch()
        }
        switchFragment(page.getFragment())
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}