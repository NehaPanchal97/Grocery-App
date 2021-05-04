package com.grocery

import android.content.Context
import android.content.SharedPreferences
import com.grocery.app.models.User

class SharedPreferenceForLogin(context: Context) {
    private val PREF_NAME = "GROCERYAPP"
    private val IS_LOGGED_IN = "IsLoggedIn"
    private val IS_ACCOUNT_DETAILS_SAVED = "IsAccountDetailsSaved"
    private val USER_ID = "userID"
    private var preference: SharedPreferences? =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor? = preference?.edit()

    fun createLoginSession(userId: String) {
        editor?.putBoolean(IS_LOGGED_IN, true)
        editor?.putString(USER_ID, userId)
        editor?.commit()
    }

    fun getUserId(): String? {
        return preference?.getString(USER_ID, null)
    }

    fun isLoggedIn(): Boolean? {
        return preference?.getBoolean(IS_LOGGED_IN, false)
    }

    fun saveAccountDetails(user: User) {
        editor?.putBoolean(IS_ACCOUNT_DETAILS_SAVED, true)
        editor?.putString("name", user.name)
        editor?.putString("name", user.address)
        editor?.putString("name", user.phone)
    }

    fun isAccountDetailsSaved(): Boolean? {
        return preference?.getBoolean(IS_ACCOUNT_DETAILS_SAVED, false)
    }

    fun clearLoginSession() {
        editor?.clear()
        editor?.commit()
    }
}