package com.grocery.app.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.grocery.app.constant.Store
import com.grocery.app.extras.Result
import com.grocery.app.models.AboutPage

class AboutPageViewModel : ViewModel() {

    private val _fetchStoreLiveData by lazy { MutableLiveData<Result<AboutPage?>>() }

    val fetchStoreLiveData: LiveData<Result<AboutPage?>>
        get() = _fetchStoreLiveData

    var store: AboutPage? = null

    fun fetchStoreInfo() {
        _fetchStoreLiveData.value = Result.loading()
        val db = Firebase.firestore
        val docRef = db.document(Store.OTHERS + "/" + Store.ABOUT_PAGE)
        docRef.get()
            .addOnSuccessListener {
                store = it.toObject(AboutPage::class.java)
                _fetchStoreLiveData.value = Result.success(store)
            }
            .addOnFailureListener {
                _fetchStoreLiveData.value = Result.error()
                Log.d("error", "Exception: ", it)
            }
    }
}