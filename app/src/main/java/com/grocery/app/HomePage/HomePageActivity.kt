package com.grocery.app.HomePage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.grocery.app.R
import com.grocery.app.activities.BaseActivity
import com.grocery.app.auth.AccountDetailsActivity
import com.grocery.app.auth.OtpScreenActivity
import com.grocery.app.auth.SsoLoginActivity
import com.grocery.app.databinding.ActivityHomeBinding
import kotlinx.android.synthetic.main.bottom_navigation_bar.*
import kotlinx.android.synthetic.main.category_group.*


class HomePageActivity: BaseActivity() {


    private lateinit var binder:ActivityHomeBinding
    var count=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this,R.layout.activity_home)

        bottomNavigationBar.background = null
        bottomNavigationBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.more->{
                    startActivity(Intent(this,AccountDetailsActivity::class.java))
                }
                R.id.home->{
                    startActivity(Intent(this,this::class.java))
                }
                R.id.order->{
//                    startActivity(Intent(this,SsoLoginActivity::class.java))
                    val fragment = CategoryTypesFragment()
                    val fragmentTransaction=supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragment_container, fragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                R.id.offer->{
                    startActivity(Intent(this,OtpScreenActivity::class.java))
                }
            }
            true
        }
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

    fun onItemClick(v:View?){
        val fragment = CategoryItemsFragment()
        val fragmentTransaction=supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun remove(v:View?){
        if(count<=0) count = 0
        else count--
        val textView = findViewById<TextView>(R.id.tv_count)
        textView.text = "$count"
    }

    fun add(v:View?){
        count++
        val textView = findViewById<TextView>(R.id.tv_count)
        textView.text = "$count"
    }

    fun backPress(v:View?){
        onBackPressed()
    }

}