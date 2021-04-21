package com.grocery.app.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPref constructor(context: Context):SharedPreferences {

    var pref: SharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE)

    companion object {
        private lateinit var sInstance: SharedPref

        fun getInstance(context: Context):SharedPref {
            if (!this::sInstance.isInitialized){
                sInstance= SharedPref(context)
            }
            return sInstance
        }
    }

    override fun contains(key: String?): Boolean {
        return pref.contains(key)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return pref.getBoolean(key,defValue)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        pref.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return pref.getInt(key,defValue)
    }

    override fun getAll(): MutableMap<String, *> {
        return pref.all
    }

    override fun edit(): SharedPreferences.Editor {
        return pref.edit()
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return pref.getLong(key,defValue)
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return pref.getFloat(key,defValue)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
        return pref.getStringSet(key, defValues)?: mutableSetOf()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        pref.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun getString(key: String?, defValue: String?): String? {
        return pref.getString(key, defValue)
    }
}