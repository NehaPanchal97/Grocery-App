package com.grocery.app.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.grocery.app.constant.HomeCarousel
import com.grocery.app.constant.Store
import com.grocery.app.extensions.isRemoteUrl
import com.grocery.app.extras.Result
import com.grocery.app.homePage.dataModel.ItemGroup
import com.grocery.app.models.Category
import com.grocery.app.utils.isBlank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val _addCatLiveData by lazy { MutableLiveData<Result<Void>>() }
    private val _catListLiveData by lazy { MutableLiveData<Result<ArrayList<Category>>>() }
    private val _homePageLiveData by lazy { MutableLiveData<Result<ArrayList<ItemGroup>>>() }
    private val _offerPageLiveData by lazy { MutableLiveData<Result<ArrayList<Category>>>() }

    val catName = MutableLiveData<String>()

    fun setTextCommunicator(msg: String) {
        catName.value = msg
    }


    val addCatLiveData: LiveData<Result<Void>>
        get() = _addCatLiveData
    val catListLiveData: LiveData<Result<ArrayList<Category>>>
        get() = _catListLiveData

    val homePageLiveData: LiveData<Result<ArrayList<ItemGroup>>>
        get() = _homePageLiveData

    val offerPageLiveData: LiveData<Result<ArrayList<Category>>>
        get() = _offerPageLiveData

    private var homeCategorySnap: QuerySnapshot? = null
    private var homeDealsSnap: QuerySnapshot? = null
    private var homeDiscountSnap: QuerySnapshot? = null

    var category = Category()


    fun addCategory() {
        _addCatLiveData.value = Result.loading()
        if (category.url.isBlank() || category.url?.isRemoteUrl == true) {
            addCategoryOnStore()
        } else {
            uploadImage()
        }
    }

    fun fetchOfferDetail() {
        _offerPageLiveData.value = Result.loading()

        Firebase.firestore.collection(Store.DISCOUNTS)
            .get()
            .addOnSuccessListener {
                val discounts = it.toObjects(Category::class.java)
                _offerPageLiveData.value = Result.success(ArrayList(discounts))
            }
            .addOnFailureListener {
                _offerPageLiveData.value = Result.error()
            }
    }

    fun fetchCategoryList(limit: Long? = null) {
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

    fun fetchHomePageData(limit: Long? = null) {
        _homePageLiveData.value = Result.loading()
        getHomeTask(Store.CATEGORIES, limit).onSuccessTask {
            homeCategorySnap = it
            getHomeTask(Store.DISCOUNTS, limit)
        }
            .onSuccessTask {
                homeDiscountSnap = it
                getHomeTask(Store.DEALS, limit)
            }
            .addOnSuccessListener {
                homeDealsSnap = it
                sanitiseHomePageData()
            }
            .addOnFailureListener {
                _homePageLiveData.value = Result.error()
            }

    }

    private fun sanitiseHomePageData() = viewModelScope.launch(Dispatchers.Default) {
        val homeData = arrayListOf<ItemGroup>()

        if (homeCategorySnap?.documents?.isNotEmpty() == true) {
            val catList = homeCategorySnap?.toObjects(Category::class.java)
            catList?.let {
                homeData.add(
                    ItemGroup(
                        carousel = HomeCarousel.CATEGORY,
                        listItem = ArrayList(it)
                    )
                )
            }
        }


//        if (homeDealsSnap?.documents?.isNotEmpty() == true) {
//            val dealList = homeDealsSnap?.toObjects(Category::class.java)
//            dealList?.let {
//                homeData.add(
//                    ItemGroup(
//                        carousel = HomeCarousel.DEALS,
//                        listItem = ArrayList(it)
//                    )
//                )
//            }
//        }

        if (homeDiscountSnap?.documents?.isNotEmpty() == true) {
            val discountList = homeDiscountSnap?.toObjects(Category::class.java)
            discountList?.let {
                homeData.add(
                    ItemGroup(
                        carousel = HomeCarousel.DISCOUNT,
                        listItem = ArrayList(it)
                    )
                )
            }
        }
        _homePageLiveData.postValue(Result.success(homeData))

    }

    private fun getHomeTask(collection: String, limit: Long? = null): Task<QuerySnapshot> {
        var query = Firebase.firestore.collection(collection)
            .orderBy(Store.RANK, Query.Direction.ASCENDING)
        limit?.let { query = query.limit(it) }
        return query.get()
    }

    private fun onCatListFetched(snapShot: QuerySnapshot?) =
        viewModelScope.launch(Dispatchers.Default) {
            val categories = snapShot?.toObjects(Category::class.java)
            _catListLiveData.postValue(Result.success(ArrayList(categories)))
        }

    private fun addCategoryOnStore() {
        val map = hashMapOf(
            Store.NAME to category.name,
            Store.RANK to category.rank,
            Store.URL to category.url
        )

        val ref = Firebase.firestore.collection(Store.CATEGORIES)
        val id = category.id ?: ref.document().id
        map[Store.ID] = id

        ref.document(id).set(map, SetOptions.merge())
            .addOnSuccessListener {
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