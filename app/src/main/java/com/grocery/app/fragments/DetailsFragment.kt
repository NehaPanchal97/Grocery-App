package com.grocery.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.grocery.app.databinding.DetailsFragmentBinding

class DetailsFragment : BaseFragment() {

    private lateinit var binder: DetailsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = DetailsFragmentBinding.inflate(inflater,container,false)
        return binder.root
    }
}