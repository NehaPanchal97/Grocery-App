package com.grocery.app.customs.pages

import android.content.Context
import androidx.fragment.app.Fragment
import com.grocery.app.R
import com.grocery.app.auth.UpdateProfileFragment
import com.grocery.app.customs.Page

class AdminSettingPage : Page() {
    override fun getToolbar(context: Context): String {
        return context.getString(R.string.profile)
    }

    override fun getFragment(): Fragment {
        return UpdateProfileFragment()
    }
}