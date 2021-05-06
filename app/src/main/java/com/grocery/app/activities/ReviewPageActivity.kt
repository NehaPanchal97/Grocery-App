//package com.grocery.app.activities
//
//import android.os.Bundle
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.tabs.TabLayout
//import com.grocery.app.R
//import com.grocery.app.adapters.DetailsAndReviewAdapter
//import kotlinx.android.synthetic.main.activity_review_page.*
//
//
//class ReviewPageActivity: AppCompatActivity() {
//   lateinit var mAdapter: DetailsAndReviewAdapter
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_review_page)
//      mAdapter = DetailsAndReviewAdapter(applicationContext, listOf("one", "two"))
//        pager.adapter=mAdapter
//        val tabLayout:TabLayout = findViewById(R.id.tabLayout)
//        tabLayout.addTab(tabLayout.newTab().setText("Tab 1"))
//        tabLayout.addTab(tabLayout.newTab().setText("Tab 2"))
//    }
//}