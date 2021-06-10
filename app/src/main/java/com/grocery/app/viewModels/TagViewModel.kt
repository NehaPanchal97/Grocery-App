package com.grocery.app.viewModels

import android.view.SurfaceControl
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.grocery.app.constant.Store
import com.grocery.app.extras.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TagViewModel : ViewModel() {

    private val _tagListLiveData by lazy { MutableLiveData<Result<ArrayList<String?>>>() }

    val tagListLiveData: LiveData<Result<ArrayList<String?>>>
        get() = _tagListLiveData

    var existingTags = arrayListOf<String?>()
    var tags = arrayListOf<String?>()

    fun fetchAllTags() {
        _tagListLiveData.value = Result.loading()
        Firebase.database.getReference(Store.TAGS)
            .get()
            .addOnCompleteListener { snapShot ->
                if (snapShot.isSuccessful) {
                    val data = snapShot.result?.getValue<ArrayList<String?>>()
                    data?.let { tags = it }
                    _tagListLiveData.value = Result.success(tags)
                } else {
                    _tagListLiveData.value = Result.error()
                }
            }
    }

    fun fetchTags(tagQuery: String?) {
        _tagListLiveData.value = Result.loading()

    }

    fun removeTag(tag: String) {
        val tags = existingTags
        tags.remove(tag)
    }

    fun shouldAddTag(text: String): Boolean {
        return !existingTags.contains(text)
    }

    fun addTag(text: String) {
        val tags = ArrayList(existingTags.filterNotNull())
        if (tags.contains(text)) {
            return
        }
        tags.add(text)
        existingTags = tags
    }

    private fun getNewTagList(): ArrayList<String?> {
        val all = hashSetOf<String?>()
        val allList = arrayListOf<String?>()
        tags.forEach {
            all.add(it)
            allList.add(it)
        }
        existingTags.forEach {
            if (!all.contains(it)) {
                all.add(it)
                allList.add(it)
            }
        }
        return allList
    }

    fun saveNewTags() = viewModelScope.launch(Dispatchers.Default) {
        val newTagList = getNewTagList()
        Firebase.database.getReference(Store.TAGS)
            .setValue(newTagList)
            .addOnCompleteListener { }
    }
}