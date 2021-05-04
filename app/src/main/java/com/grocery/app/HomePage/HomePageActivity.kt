package com.grocery.app.HomePage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.R
import com.grocery.app.activities.UpdateProfileActivity
import com.grocery.app.auth.UpdateProfileFragment
import com.grocery.app.constant.USER
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extras.Result
import com.grocery.app.models.User
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.AuthViewModel
import kotlinx.android.synthetic.main.bottom_navigation_bar.*


class HomePageActivity : AppCompatActivity() {

    private val prefManager by lazy { PrefManager.getInstance(this) }
    private lateinit var viewModel: AuthViewModel
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
        viewModel.fetchUserLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> {

                }
                Result.Status.SUCCESS -> {
                    currentFocus?.showSuccess("updated")
                    prefManager.put(USER, it.data)
                    it.data?.let {
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
        val fragment = CategoryTypesFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    fun backPress(v: View?) {
        onBackPressed()
    }

}