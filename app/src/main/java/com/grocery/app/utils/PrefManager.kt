package com.grocery.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.grocery.app.extensions.toJson
import com.grocery.app.extensions.toObj

class PrefManager private constructor(context: Context) {


    private var _pref =
        context.getSharedPreferences("Grocery_pref", Context.MODE_PRIVATE)

    private val keysToRetain = setOf<String>()

    companion object {
        private lateinit var sInstance: PrefManager
        private val lock = Any()

        fun getInstance(context: Context): PrefManager {
            if (!::sInstance.isInitialized) {
                synchronized(lock) {
                    if (!::sInstance.isInitialized) {
                        sInstance = PrefManager(context.applicationContext)
                    }
                }
            }
            return sInstance
        }
    }

    fun putString(key: String, value: String?) {
        _pref.edit {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String, default: String? = null): String? {
        return _pref.getString(key, default)
    }


    inline fun <reified T> get(key: String, default: T? = null): T? {
        return getString(key)
            .toObj(default)
    }

    fun put(key: String, value: Any?) {
        _pref.edit {
            putString(key, value?.toJson())
            apply()
        }
    }

    fun contains(key: String): Boolean {
        return _pref.contains(key)
    }

    fun remove(key: String) {
        _pref.edit {
            remove(key)
            apply()
        }
    }

    fun addOnPrefChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        _pref.registerOnSharedPreferenceChangeListener(listener)
    }

    fun removePrefChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        _pref.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun clear() {
        val persisted = keysToRetain.filter { _pref.contains(it) }
            .map { Pair(it, _pref.getString(it, null)) }
        _pref.edit().clear().apply()
        persisted.forEach {
            _pref.edit().putString(it.first, it.second).apply()
        }
    }

}