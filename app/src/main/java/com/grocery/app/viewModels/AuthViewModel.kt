package com.grocery.app.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.grocery.app.constant.CART
import com.grocery.app.constant.Store
import com.grocery.app.constant.USER
import com.grocery.app.extensions.authUser
import com.grocery.app.extensions.toObj
import com.grocery.app.models.User
import com.grocery.app.extras.Result
import com.grocery.app.models.Cart
import com.grocery.app.utils.isBlank

class AuthViewModel : ViewModel() {

    private val _updateUserLiveData by lazy { MutableLiveData<Result<Void>>() }

    val updateUserLiveData: LiveData<Result<Void>>
        get() = _updateUserLiveData

    private val _fetchUserLiveData by lazy { MutableLiveData<Result<User?>>() }

    private val _syncLiveData by lazy { MutableLiveData<Result<Pair<String, Any?>>>() }

    val fetchUserLiveData: LiveData<Result<User?>>
        get() = _fetchUserLiveData

    val syncLiveData: LiveData<Result<Pair<String, Any?>>>
        get() = _syncLiveData

    var user: User? = null

    fun updateUser() {
        _updateUserLiveData.value = Result.loading()
        if (user?.url.isBlank() || user?.url?.startsWith("https://") == true) {
            updateUserOnStore()
        } else {
            uploadImage()
        }
    }

    private fun updateUserOnStore() {
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
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                _updateUserLiveData.value = Result.success()
            }
            .addOnFailureListener {
                _updateUserLiveData.value = Result.error()
            }
    }

    fun fetchUserInfo() {
        _fetchUserLiveData.value = Result.loading()
        val db = Firebase.firestore
        val docRef = db.collection(Store.USERS)
            .document(authUser?.uid ?: "")
        docRef.get()
            .addOnSuccessListener { document ->
                Log.d("exists", "Document Snapshot: ${document.data}")
                user = if (document.exists()) {
                    document.toObj<User>()
                } else null
                _fetchUserLiveData.value = Result.success(user)
            }
            .addOnFailureListener { exception ->
                _fetchUserLiveData.value = Result.error()
                Log.d("error", "Exception: ", exception)
            }
    }

    private fun uploadImage() {
        val file = Uri.parse(user?.url)
        val fileName = "User_" + System.currentTimeMillis() + ".jpg"
        val fileRef = Firebase.storage.reference
            .child("images/$fileName")

        fileRef.putFile(file)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                fileRef.downloadUrl
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user?.url = it.result.toString()
                    updateUserOnStore()
                } else {
                    _updateUserLiveData.value = Result.error()
                }
            }
    }

    fun syncUser() {
        val db = Firebase.firestore

        db.document(Store.USERS + "/" + authUser?.uid)
            .get()
            .onSuccessTask {
                if (it?.exists() == true) {
                    _syncLiveData.value = Result.success(Pair(USER, it.toObject(User::class.java)))
                }
                db.collection("${Store.USERS}/${authUser?.uid}/${Store.CART}")
                    .get()
            }
            .addOnCompleteListener { snapshot ->
                if (snapshot.isSuccessful) {
                    if (snapshot.result?.isEmpty == false) {
                        val cart = snapshot.result?.toObjects(Cart::class.java)?.firstOrNull()
                        cart?.let { _syncLiveData.value = Result.success(Pair(CART, it)) }
                    }
                } else {
                    _syncLiveData.value = Result.error()
                }
            }
    }

    fun syncCart() {
        Firebase.firestore
    }


}