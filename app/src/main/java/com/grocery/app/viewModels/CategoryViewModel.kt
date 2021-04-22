package com.grocery.app.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.grocery.app.constant.Store
import com.grocery.app.extras.Result
import com.grocery.app.models.Category
import com.grocery.app.utils.isBlank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val _addCatLiveData by lazy { MutableLiveData<Result<Void>>() }
    private val _catListLiveData by lazy { MutableLiveData<Result<ArrayList<Category>>>() }


    val addCatLiveData: LiveData<Result<Void>>
        get() = _addCatLiveData
    val catListLiveData: LiveData<Result<ArrayList<Category>>>
        get() = _catListLiveData


    var categoryImage: String? = null
    var name: String? = null
    var rank: Int = 0

    fun addCategory(name: String?, rank: Int?) {
        this.name = name
        this.rank = rank ?: 0
        _addCatLiveData.value = Result.loading()
        if (categoryImage.isBlank()) {
            addCategoryOnStore()
        } else {
            uploadImage()
        }
    }

    fun fetchCategoryList() {
        _catListLiveData.value = Result.loading()
        Firebase.firestore.collection(Store.CATEGORIES)
            .orderBy(Store.RANK, Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { snapShot ->
                onCatListFetched(snapShot)
            }
            .addOnFailureListener {
                _catListLiveData.value = Result.error()
            }

    }

    private fun onCatListFetched(snapShot: QuerySnapshot?) =
        viewModelScope.launch(Dispatchers.Default) {
            val categories = arrayListOf<Category>()
            snapShot?.let { it ->
                it.documents.forEach { document ->
                    document.toObject<Category>()?.let { categories.add(it) }
                }
            }
            _catListLiveData.postValue(Result.success(categories))

        }

    private fun addCategoryOnStore() {
        val map = hashMapOf(
            Store.NAME to name,
            Store.RANK to rank,
            Store.URL to categoryImage
        )
        Firebase.firestore.collection(Store.CATEGORIES)
            .add(map)
            .addOnSuccessListener {
                _addCatLiveData.value = Result.success()
            }
            .addOnFailureListener {
                _addCatLiveData.value = Result.error()

            }

    }

    private fun uploadImage() {
        val file = Uri.parse(categoryImage)
        val fileName = "Categories_" + System.currentTimeMillis() + ".jpg"
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
                    categoryImage = it.result.toString()
                    addCategoryOnStore()
                } else {
                    _addCatLiveData.value = Result.error()
                }
            }
    }
}