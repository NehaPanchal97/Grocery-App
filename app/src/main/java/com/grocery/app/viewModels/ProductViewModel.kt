package com.grocery.app.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.grocery.app.constant.CartAction
import com.grocery.app.constant.DEFAULT_PAGE_SIZE
import com.grocery.app.constant.DISCOUNT
import com.grocery.app.constant.Store
import com.grocery.app.extensions.*
import com.grocery.app.extras.Result
import com.grocery.app.models.Cart
import com.grocery.app.models.Category
import com.grocery.app.models.Product
import com.grocery.app.utils.isBlank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {


    private val _addOrUpdateProductLiveData by lazy { MutableLiveData<Result<Product>>() }
    private val _productListLiveData by lazy { MutableLiveData<Result<ArrayList<Product>>>() }
    private val _similarProductListLiveData by lazy { MutableLiveData<Result<ArrayList<Product>>>() }
    private val _searchProductLiveData by lazy { MutableLiveData<Result<ArrayList<Product>>>() }
    private val _updateCartLiveData by lazy { MutableLiveData<Result<Void>>() }

    val addOrUpdateProductLiveData: LiveData<Result<Product>>
        get() = _addOrUpdateProductLiveData

    val productListLiveData: LiveData<Result<ArrayList<Product>>>
        get() = _productListLiveData

    val updateCart: LiveData<Result<Void>>
        get() = _updateCartLiveData

    val similarListLiveData: LiveData<Result<ArrayList<Product>>>
        get() = _similarProductListLiveData

    val searchProductLiveData: LiveData<Result<ArrayList<Product>>>
        get() = _searchProductLiveData

    var catList = arrayListOf<Category>()
    var product = Product()
    var filterByCat: Category? = null
    var cartMap = hashMapOf<String, Product?>()
    lateinit var cart: Cart
    var cartUpdated = false
    var hasMoreProduct = true
    var lastProductSnap: DocumentSnapshot? = null
    var discount: Double? = null
    var orderBy = Query.Direction.DESCENDING
    var searchKey: String? = null

    val loadMore
        get() = lastProductSnap != null


    fun fetchProductList(
        initialFetch: Boolean = true,
        orderByKey: String = Store.DISCOUNT,
        limit: Long? = DEFAULT_PAGE_SIZE
    ) {
        if (initialFetch) {
            hasMoreProduct = true
            lastProductSnap = null
//            batchUpdate()
        }
        _productListLiveData.value = Result.loading()
        var query = Firebase.firestore.collection(Store.PRODUCTS)
            .limit(limit ?: DEFAULT_PAGE_SIZE)
        if (orderByKey == Store.DISCOUNT) {
            query = query.whereGreaterThanOrEqualTo(Store.DISCOUNT, discount ?: 0.0)
        }
        query = query.orderBy(orderByKey, orderBy)
        filterByCat?.let {
            query = query.whereEqualTo(Store.CATEGORY_ID, filterByCat?.id)
        }
        if (!initialFetch) {
            query = query.startAfter(lastProductSnap?.get(Store.CREATED_AT))
        }
        if (searchKey?.isNotEmpty() == true) {
            query = query.whereArrayContains("search_keys", searchKey ?: "")
        }
        query.get().addOnSuccessListener { snapShot ->
            val size = snapShot?.size() ?: 0
            limit?.let {
                hasMoreProduct = size >= it
            }
            onProductFetched(snapShot)

        }
            .addOnFailureListener {
                _productListLiveData.value = Result.error()
            }
    }


    fun batchUpdate() {
        //To update batch
    }

    fun fetchProductWithTag() {
        val tags = product.tags ?: arrayListOf()
        if (tags.isEmpty()) {
            return
        }
        _similarProductListLiveData.value = Result.loading()
        val ref = Firebase.firestore.collection(Store.PRODUCTS)
        var query = ref.whereNotEqualTo("id", product.id)
        query = query.whereArrayContainsAny("tags", tags)
        query.get()
            .addOnSuccessListener { snapShot ->
                val products = snapShot.toObjects(Product::class.java)
                _similarProductListLiveData.value = Result.success(ArrayList(products))
            }
            .addOnFailureListener {
                _similarProductListLiveData.value = Result.error()
            }
    }

    fun fetchProductWithKey(keys: String) {
        _searchProductLiveData.value = Result.loading()

        Firebase.firestore.collection(Store.PRODUCTS)
            .whereArrayContains("search_keys", keys)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                _searchProductLiveData.value = Result.success(ArrayList(products))
            }
    }


    fun updateCart(
        product: Product?,
        cartAction: CartAction = CartAction.QUANTITY_INCREASED
    ) {
        _updateCartLiveData.value = Result.loading()
        evaluateCartUpdate(product, cartAction)
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

    private fun evaluateCartItemCount(cartAction: CartAction, oldCount: Int): Int {
        return when (cartAction) {
            CartAction.QUANTITY_INCREASED -> oldCount + 1
            CartAction.QUANTITY_DECREASED -> oldCount - 1
            else -> 0
        }
    }

    private fun evaluateCartUpdate(product: Product?, cartAction: CartAction) {

        val item = product?.clone()
        val itemCount = cartMap[product?.id]?.count ?: 0
        item?.count = evaluateCartItemCount(cartAction, itemCount)
        item?.total = item?.count?.times(item.price ?: 0.0)?.round(2)
        item?.totalDiscount = item?.total?.percentage(item.discount ?: 0.0)?.round(2)

        if (item?.count == 0) {
            cartMap.remove(product.id ?: "")
        } else {
            cartMap[product?.id ?: ""] = item
        }

        val items = arrayListOf<Product>()
        var total = 0.0
        var totalDiscount = 0.0
        cartMap.forEach {
            it.value?.let { product ->
                total += product.total ?: 0.0
                totalDiscount += product.totalDiscount ?: 0.0
                items.add(product)
            }
        }
        cart.total = total
        cart.totalDiscount = totalDiscount
        cart.payableAmount = total.minus(totalDiscount)
        cart.items = items
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
            delay(200)
            if (snapShot?.isEmpty == false) {
                val documents = snapShot.documents
                lastProductSnap = documents.getOrNull(documents.size - 1)
            }
        }

    fun addOrUpdateProduct() {

        _addOrUpdateProductLiveData.value = Result.loading()
        if (product.url.isBlank() || product.url?.isRemoteUrl == true) {
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

        val productId = product.id ?: ref.document().id
        val time = FieldValue.serverTimestamp()
        val map = hashMapOf(
            Store.NAME to product.name,
            Store.DESCRIPTION to product.description,
            Store.PRICE to product.price,
            Store.ACTIVE to (product.active ?: true),
            Store.CATEGORY_ID to product.categoryId,
            Store.URL to product.url,
            Store.TAGS to product.tags,
            Store.DISCOUNT to (product.discount ?: 0.0),
            Store.ID to productId,
            Store.UPDATED_AT to time
        )
        if (product.id.isBlank()) {
            map[Store.CREATED_AT] = time
        }

        if ((product.name?.length ?: 0) > 2) {
            map[Store.SEARCH_KEYS] = product.name?.searchKeys
        }

        ref.document(productId)
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                _addOrUpdateProductLiveData.value = Result.success()
            }
            .addOnFailureListener {
                _addOrUpdateProductLiveData.value = Result.error()
            }
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

    fun initCartWith(cart: Cart) {
        this.cart = cart
        cartMap.clear()
        initCart()
    }

    fun resetCart() {
        cart = Cart()
        cartMap.clear()
    }

}