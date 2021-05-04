package com.grocery.app.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.grocery.app.constant.Store
import com.grocery.app.extensions.authUser
import com.grocery.app.extensions.toObj
import com.grocery.app.models.User
import com.grocery.app.extras.Result

class AuthViewModel : ViewModel() {

    private val _updateUserLiveData by lazy { MutableLiveData<Result<Void>>() }

    val updateUserLiveData: LiveData<Result<Void>>
        get() = _updateUserLiveData

    private val _fetchUserLiveData by lazy { MutableLiveData<Result<User?>>() }

    val fetchUserLiveData: LiveData<Result<User?>>
    get() = _fetchUserLiveData

    var user: User? = null

    fun updateUserInfo() {
        _updateUserLiveData.value = Result.loading()
        val map = mapOf(
            "name" to user?.name,
            "url" to user?.url,
            "phone" to user?.phone,
            "address" to user?.address,
            "id" to user?.id
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

    fun fetchUserInfo(){
        _fetchUserLiveData .value = Result.loading()
        val db = Firebase.firestore
                val docRef = db.collection(Store.USERS)
            .document(authUser?.uid ?: "")
            docRef.get()
            .addOnSuccessListener {document ->
                 Log.d("exists","Document Snapshot: ${document.data}")
               val user= document.toObj<User>()
                    this.user = user
                    _fetchUserLiveData.value = Result.success(user)

//                    user.address = document.getString("address")
//                    user.name = document.getString("name")
//                    user.phone = document.getString("phone")

            }
                .addOnFailureListener { exception ->
                    _fetchUserLiveData.value = Result.error()
                    Log.d("error","Exception: ",exception)
                }
    }




}