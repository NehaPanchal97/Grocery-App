package com.grocery.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.grocery.app.databinding.ReviewFragmentBinding

class ReviewFragment : BaseFragment() {
    private lateinit var binder: ReviewFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = ReviewFragmentBinding.inflate(inflater, container, false)
        return binder.root
    }
}