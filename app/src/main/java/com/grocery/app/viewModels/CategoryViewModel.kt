package com.grocery.app.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.grocery.app.constant.CATEGORY
import com.grocery.app.constant.Store
import com.grocery.app.extensions.toObj
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

    var category = Category()

    fun addCategory() {
        _addCatLiveData.value = Result.loading()
        if (category.url.isBlank() || category.url?.startsWith("https://") == true) {
            addCategoryOnStore()
        } else {
            uploadImage()
        }
    }

    fun fetchCategoryList(limit: Long? = Long.MAX_VALUE) {
        _catListLiveData.value = Result.loading()
        val query = Firebase.firestore.collection(Store.CATEGORIES)
            .orderBy(Store.RANK, Query.Direction.ASCENDING)
        limit?.let { query.limit(limit) }
        query.get()
            .addOnSuccessListener { snapShot ->
                onCatListFetched(snapShot)
            }
            .addOnFailureListener {
                _catListLiveData.value = Result.error()
            }

    }

    private fun onCatListFetched(snapShot: QuerySnapshot?) =
        viewModelScope.launch(Dispatchers.Default) {
            val categories = snapShot?.toObjects(Category::class.java)

//            val categories = arrayListOf<Category>()
//            snapShot?.let { it ->
//                it.documents.forEach { document ->
//                    document.toObj<Category>()?.let {
//                        val cat = it.apply { id = document.id }
//                        categories.add(cat)
//                    }
//                }
//            }
            _catListLiveData.postValue(Result.success(ArrayList(categories)))

        }

    private fun addCategoryOnStore() {
        val map = hashMapOf(
            Store.NAME to category.name,
            Store.RANK to category.rank,
            Store.URL to category.url
        )
        val editMode = !category.id.isBlank()

        val ref = Firebase.firestore.collection(Store.CATEGORIES)
        val id = category.id ?: ref.document().id
        map[Store.ID] = id

        val task = ref.document(id).set(map)


        task.addOnSuccessListener {
            _addCatLiveData.value = Result.success()
        }
            .addOnFailureListener {
                _addCatLiveData.value = Result.error()

            }

    }

    private fun uploadImage() {
        val file = Uri.parse(category.url)
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
                    category.url = it.result.toString()
                    addCategoryOnStore()
                } else {
                    _addCatLiveData.value = Result.error()
                }
            }
    }
}