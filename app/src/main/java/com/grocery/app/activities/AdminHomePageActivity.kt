package com.grocery.app.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.grocery.app.R
import com.grocery.app.customs.PageFactory
import com.grocery.app.databinding.ActivityAdminHomePageBinding
import com.grocery.app.extensions.cast
import androidx.appcompat.widget.Toolbar

class AdminHomePageActivity : BaseActivity() {

    private lateinit var binder: ActivityAdminHomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_admin_home_page)
        setupView()
    }

    private fun setupView() {
        switchPage(R.id.home)

        val navBar = findViewById<BottomNavigationView>(R.id.nav_bar)
        navBar.setOnNavigationItemSelectedListener(_navItemSelectedListener)
        binder.toolBar.setOnMenuItemClickListener(_menuItemClickListener)
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