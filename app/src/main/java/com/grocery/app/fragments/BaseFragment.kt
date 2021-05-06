package com.grocery.app.fragments

import android.annotation.SuppressLint
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.grocery.app.R
import com.grocery.app.activities.BaseActivity
import com.grocery.app.extensions.cast

open class BaseFragment : Fragment() {

    @SuppressLint("ResourceType")
    protected fun setupToolbar(
        toolBar: MaterialToolbar,
        enableBack: Boolean = true,
        @MenuRes menuId: Int = -1
    ) {
        activity?.cast<BaseActivity>()
            ?.setupToolbar(toolBar, enableBack, menuId)
    }

    open fun loading(show: Boolean) {

    }
}