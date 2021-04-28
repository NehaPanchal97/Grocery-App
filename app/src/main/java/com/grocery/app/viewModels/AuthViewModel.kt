package com.grocery.app.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.grocery.app.constant.Store
import com.grocery.app.extensions.authUser
import com.grocery.app.models.User
import com.grocery.app.extras.Result

class AuthViewModel : ViewModel() {

    private val _updateUserLiveData by lazy { MutableLiveData<Result<Void>>() }

    val updateUserLiveData: LiveData<Result<Void>>
        get() = _updateUserLiveData

    var user = User()

    fun updateUserInfo() {
        _updateUserLiveData.value = Result.loading()
        val map = mapOf(
            "name" to user.name,
            "url" to user.url,
            "phone" to user.phone,
            "address" to user.address,
            "id" to user.id
        )
        Firebase.firestore
            .collection(Store.USERS)
            .document(authUser?.uid ?: "")
            .set(map)
            .addOnSuccessListener {
                _updateUserLiveData.value = Result.success()
            }
            .addOnFailureListener {
                _updateUserLiveData.value = Result.error()
            }
    }


}