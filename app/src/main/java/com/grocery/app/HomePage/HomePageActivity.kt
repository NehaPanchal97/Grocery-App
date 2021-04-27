package com.grocery.app.HomePage

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.grocery.app.R
import kotlinx.android.synthetic.main.bottom_navigation_bar.*
import kotlinx.android.synthetic.main.category_group.*


class HomePageActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNavigationBar.background = null
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    fun onClickBtnMore(v: View?) {
        val fragment = CategoryTypesFragment()
        val fragmentTransaction=supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    fun backPress(v:View?){
        onBackPressed()
    }

}