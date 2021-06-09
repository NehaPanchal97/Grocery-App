package com.grocery.app.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grocery.app.extras.Result

class TagViewModel : ViewModel() {

    private val _tagListLiveData by lazy { MutableLiveData<Result<ArrayList<String?>>>() }

    val tagListLiveData: LiveData<Result<ArrayList<String?>>>
        get() = _tagListLiveData

    var existingTags = arrayListOf<String?>()
    val tags = arrayListOf<String?>()

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
    }
}