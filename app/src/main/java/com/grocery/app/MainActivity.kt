package com.grocery.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.grocery.app.HomePage.Fragment
import kotlinx.android.synthetic.main.bottom_navigation_bar.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        doFragmentTransaction(Fragment(),false,"Fragment",null)
    }

    private fun doFragmentTransaction(
        fragment: androidx.fragment.app.Fragment,
        addToBackStack: Boolean,
        tag: String,
        bundle: Bundle?
    ) {
        if (bundle != null) {
            fragment.arguments = bundle
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.rootView, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(tag)
        }
        transaction.commit()
    }


}