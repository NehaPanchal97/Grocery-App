package com.grocery.app.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.grocery.app.R
import kotlinx.android.synthetic.main.activity_review_page.view.*
import kotlinx.android.synthetic.main.review_fragment.view.*

//class DetailsAndReviewAdapter(private val context: Context, private val details:List<String>): RecyclerView.Adapter<DetailsAndReviewAdapter.DetailsVH>(){
//
//    class DetailsVH(view: View) : RecyclerView.ViewHolder(view)
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsVH {
//      return  DetailsVH(LayoutInflater.from(context).inflate(R.layout.review_fragment, parent, false))
//    }
//
//    override fun onBindViewHolder(holder: DetailsVH, position: Int) {
//        holder.itemView.tvReviews.text =  details[position]
//
//    }
//
//    override fun getItemCount(): Int = details.size
//
//}
//
//class DetailsAndReviewAdapter(@NonNull fragment: FragmentActivity,
//                              private val numberOfTab: List<String>,
//                              private val bundles: ArrayList<Bundle>
//) : FragmentStateAdapter(fragment) {
//    override fun getItemCount(): Int {
//        return numberOfTab.size
//    }
//    override fun createFragment(position: Int): Fragment {
//        return .newInstance(bundles)
//    )

