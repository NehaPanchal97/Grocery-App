package com.grocery.app.homePage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.R
import com.grocery.app.activities.UpdateProfileActivity
import com.grocery.app.constant.USER
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extras.Result
import com.grocery.app.models.User
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.AuthViewModel
import kotlinx.android.synthetic.main.bottom_navigation_bar.*
import kotlinx.android.synthetic.main.specific_item_with_price.*
import kotlinx.android.synthetic.main.specific_itemgroup_in_product.*


class HomePageActivity : AppCompatActivity() {

    private val prefManager by lazy { PrefManager.getInstance(this) }
    private lateinit var viewModel: AuthViewModel
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        observeData()
//       switchFragment()
        val user = prefManager.get<User>(USER)
        user?.let {
            switchFragment()
        } ?: kotlin.run { viewModel.fetchUserInfo() }
    }


    private fun switchFragment(fragment: Fragment = HomeFragment()) {
        bottomNavigationBar.background = null
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
                        switchFragment()
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

    fun remove(v: View?) {
        if (count <= 0) count = 0
        else count--
        val textView = findViewById<TextView>(R.id.tv_count)
        textView.text = "$count"
    }

    fun add(v: View?) {
        count++
        val textView = findViewById<TextView>(R.id.tv_count)
        textView.text = "$count"
    }
}