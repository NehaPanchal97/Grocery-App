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
import com.grocery.app.models.Product
import com.grocery.app.utils.isBlank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel : ViewModel() {


    private val _addOrUpdateProductLiveData by lazy { MutableLiveData<Result<Product>>() }
    private val _productListLiveData by lazy { MutableLiveData<Result<ArrayList<Product>>>() }

    val addOrUpdateProductLiveData: LiveData<Result<Product>>
        get() = _addOrUpdateProductLiveData

    val productListLiveData: LiveData<Result<ArrayList<Product>>>
        get() = _productListLiveData


    var catList = arrayListOf<Category>()
    var product = Product()


    fun fetchProductList() {
        _productListLiveData.value = Result.loading()
        Firebase.firestore.collection(Store.PRODUCTS)
            .get()
            .addOnSuccessListener { snapShot ->
                onProductFetched(snapShot)
            }
            .addOnFailureListener {
                _productListLiveData.value = Result.error()
            }
    }

    private fun onProductFetched(snapShot: QuerySnapshot?) =
        viewModelScope.launch(Dispatchers.Default) {
            val products = arrayListOf<Product>()
            snapShot?.documents?.forEach { document ->
                document?.toObject<Product>()?.let {
                    it.id = document.id
                    products.add(it)
                }
            }
            _productListLiveData.postValue(Result.success(products))
        }

    fun addOrUpdateProduct() {
        if (product.url.isBlank() || product.url?.startsWith("https://") == true) {
            addOrUpdateProductOnStore()
        } else {
            uploadProductImage()
        }
    }

    private fun uploadProductImage() {
        val file = Uri.parse(product.url)
        val fileName = "Products_" + System.currentTimeMillis() + ".jpg"
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
                    product.url = it.result.toString()
                    addOrUpdateProductOnStore()
                } else {
                    _addOrUpdateProductLiveData.value = Result.error()
                }
            }
    }

    private fun addOrUpdateProductOnStore() {
        _addOrUpdateProductLiveData.value = Result.loading()
        val map = hashMapOf(
            Store.NAME to product.name,
            Store.DESCRIPTION to product.description,
            Store.PRICE to product.price,
            Store.ACTIVE to product.active,
            Store.CATEGORY_ID to product.categoryId,
            Store.URL to product.url
        )
        val baseTask = Firebase.firestore.collection(Store.PRODUCTS)

        val task = if (product.id.isBlank()) baseTask.add(map)
        else baseTask
            .document(product.id ?: "")
            .set(map)

        task.addOnSuccessListener {
            _addOrUpdateProductLiveData.value = Result.success()
        }
            .addOnFailureListener {
                _addOrUpdateProductLiveData.value = Result.error()
            }
    }
}