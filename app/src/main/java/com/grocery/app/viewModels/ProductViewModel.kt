package com.grocery.app.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.grocery.app.constant.Store
import com.grocery.app.extensions.authUser
import com.grocery.app.extensions.toObj
import com.grocery.app.extras.Result
import com.grocery.app.models.Cart
import com.grocery.app.models.Category
import com.grocery.app.models.Product
import com.grocery.app.utils.isBlank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {


    private val _addOrUpdateProductLiveData by lazy { MutableLiveData<Result<Product>>() }
    private val _productListLiveData by lazy { MutableLiveData<Result<ArrayList<Product>>>() }
    private val _updateCartLiveData by lazy { MutableLiveData<Result<Void>>() }

    val addOrUpdateProductLiveData: LiveData<Result<Product>>
        get() = _addOrUpdateProductLiveData

    val productListLiveData: LiveData<Result<ArrayList<Product>>>
        get() = _productListLiveData

    val updateCart: LiveData<Result<Void>>
        get() = _updateCartLiveData

    var catList = arrayListOf<Category>()
    var product = Product()
    var filterByCat: Category? = null
    var cartMap = hashMapOf<String, Product?>()
    lateinit var cart: Cart


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

    fun updateCart(
        product: Product?,
        isAddition: Boolean = true
    ) {
        _updateCartLiveData.value = Result.loading()
        if (isAddition) {
            evaluateAddInCart(product)
        } else {
            evaluateRemoveFromCart(product)
        }
        val ref = Firebase.firestore
            .collection("${Store.USERS}/${authUser?.uid}/${Store.CART}")
        if (cart.id?.isEmpty() != false) {
            cart.id = ref.document().id
        }
        ref.document(cart.id ?: "")
            .set(cart, SetOptions.merge())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _updateCartLiveData.value = Result.success()
                } else {
                    _updateCartLiveData.value = Result.error()
                }
            }
    }

    private fun evaluateRemoveFromCart(product: Product?) {
        var cartTotal = cart.total ?: 0.0
        cartMap[product?.id ?: ""]?.let {
            val count = it.count ?: 0
            if (count < 2) {
                cartMap.remove(it.id)
                cart.items?.remove(it)
                cartTotal -= (it.total ?: 0.0)
            } else {
                it.count = count - 1
                cartTotal -= it.total ?: 0.0
                it.total = (it.price ?: 0.0) * (it.count ?: 0)
                cartTotal += it.total ?: 0.0
            }
        }
        cart.total = cartTotal
    }

    private fun evaluateAddInCart(product: Product?) {
        var cartTotal = cart.total ?: 0.0
        if (cartMap.containsKey(product?.id ?: "")) {
            val item = cartMap[product?.id ?: ""]
            item?.let {
                it.price = product?.price
                it.count = (it.count ?: 0) + 1
                cartTotal -= (it.total ?: 0.0)
                it.total = (it.price ?: 0.0) * (it.count ?: 0)
                cartTotal += (it.total ?: 0.0)
            }
        } else {
            val items = cart.items ?: arrayListOf()
            product?.let {
                it.count = 1
                it.total = it.price
                items.add(it)
                cart.items = items
                cartTotal += (it.total ?: 0.0)
                cartMap[it.id ?: ""] = it
            }
        }
        cart.total = cartTotal
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
        val ref = Firebase.firestore.collection(Store.PRODUCTS)
        if (product.id.isBlank()) {
            product.id = ref.document().id
        }
        val map = hashMapOf(
            Store.NAME to product.name,
            Store.DESCRIPTION to product.description,
            Store.PRICE to product.price,
            Store.ACTIVE to product.active,
            Store.CATEGORY_ID to product.categoryId,
            Store.URL to product.url,
            Store.TAGS to product.tags,
            Store.ID to product.id
        )

        if ((product.name?.length ?: 0) > 2) {
            map[Store.SEARCH_KEYS] = createSearchKeys(product.name ?: "")
        }

        ref.document(product.id ?: "")
            .set(map)
            .addOnSuccessListener {
                _addOrUpdateProductLiveData.value = Result.success()
            }
            .addOnFailureListener {
                _addOrUpdateProductLiveData.value = Result.error()
            }
    }

    private fun createSearchKeys(key: String): ArrayList<String> {
        val keys = arrayListOf<String>()
        val words = key.trim().split(" ")
        words.forEach { word ->
            if (word.length > 2) {
                for (i in 3..word.length) {
                    keys.add(word.substring(0, i).toLowerCase())
                }
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

    fun initCart() {
        cart.items?.forEach {
            cartMap[it.id ?: ""] = it
        }
    }

}