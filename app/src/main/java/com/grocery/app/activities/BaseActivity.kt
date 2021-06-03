package com.grocery.app.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.grocery.app.R


open class BaseActivity : AppCompatActivity() {

    @SuppressLint("ResourceType")
    fun setupToolbar(
        toolBar: MaterialToolbar,
        enableBack: Boolean = true,
        @MenuRes menuId: Int = -1
    ) {
        if (enableBack) {
            toolBar.setNavigationIcon(R.drawable.ic_back)
            toolBar.setNavigationOnClickListener { onBackPressed() }
        }
        if (menuId > 0) {
            toolBar.inflateMenu(menuId)
        }
    }

    open fun loading(show: Boolean) {

    }


}