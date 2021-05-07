package com.grocery.app.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.grocery.app.constant.Store
import com.grocery.app.extensions.toObj
import com.grocery.app.extras.Result
import com.grocery.app.models.Category
import com.grocery.app.models.Product
import com.grocery.app.utils.isBlank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class ProductViewModel : ViewModel() {


    private val _addOrUpdateProductLiveData by lazy { MutableLiveData<Result<Product>>() }
    private val _productListLiveData by lazy { MutableLiveData<Result<ArrayList<Product>>>() }

    val addOrUpdateProductLiveData: LiveData<Result<Product>>
        get() = _addOrUpdateProductLiveData

    val productListLiveData: LiveData<Result<ArrayList<Product>>>
        get() = _productListLiveData


    var catList = arrayListOf<Category>()
    var product = Product()
    var filterByCat: Category? = null


    fun fetchProductList() {
        _productListLiveData.value = Result.loading()
        val ref = Firebase.firestore.collection(Store.PRODUCTS)
        val task = filterByCat?.let {
            ref.whereEqualTo(Store.CATEGORY_ID, filterByCat?.id)
                .get()
        } ?: kotlin.run { ref.get() }
        task.addOnSuccessListener { snapShot ->
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
                document?.toObj<Product>()?.let {
                    it.id = document.id
                    products.add(it)
                }
            }
            _productListLiveData.postValue(Result.success(products))
        }

    fun addOrUpdateProduct() {

        _addOrUpdateProductLiveData.value = Result.loading()
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
        val map = hashMapOf(
            Store.NAME to product.name,
            Store.DESCRIPTION to product.description,
            Store.PRICE to product.price,
            Store.ACTIVE to product.active,
            Store.CATEGORY_ID to product.categoryId,
            Store.URL to product.url,
            Store.TAGS to product.tags
        )

        if ((product.name?.length ?: 0) > 2) {
            map[Store.SEARCH_KEYS] = createSearchKeys(product.name ?: "")
        }

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

    private fun createSearchKeys(key: String): ArrayList<String> {
        val keys = arrayListOf<String>()
        val words = key.split(" ")
        if (words.isNotEmpty()) {
            val firstWord = words[0]
            if (firstWord.length < 3) {
                return keys
            }
            for (i in 3..firstWord.length) {
                keys.add(firstWord.substring(0, i).toLowerCase())
            }
        }
        return keys
    }

    fun shouldAddTag(text: String): Boolean {
        return product.tags?.contains(text) != true
    }

    fun addTag(text: String) {
        val tags = ArrayList(product.tags?.filterNotNull() ?: arrayListOf())
        if (tags.contains(text)) {
            return
        }
        tags.add(text)
        product.tags = tags
    }

    fun removeTag(tag: String) {
        val tags = product.tags ?: arrayListOf()
        tags.remove(tag)
        product.tags = tags
    }
}