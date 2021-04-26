package com.grocery.app.HomePage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grocery.app.R

class HomePageActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        doFragmentTransaction(HomeFragment(),false,"Fragment",null)
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