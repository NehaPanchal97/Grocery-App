package com.grocery.app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.grocery.app.R
import com.grocery.app.adapters.TagListAdapter
import com.grocery.app.constant.TAGS
import com.grocery.app.databinding.ActivitySelectTagBinding
import com.grocery.app.extensions.hideKeyboard
import com.grocery.app.extensions.visible
import com.grocery.app.extras.Result
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.viewModels.TagViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SelectTagActivity : BaseActivity() {

    private lateinit var binder: ActivitySelectTagBinding
    private lateinit var viewModel: TagViewModel
    private lateinit var tagListAdapter: TagListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_up, R.anim.hold)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_select_tag)
        viewModel = ViewModelProvider(this).get(TagViewModel::class.java)
        setupView()
        observe()
        viewModel.fetchAllTags()
    }

    private fun observe() {
        viewModel.tagListLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    binder.loading = true
                    binder.error = null
                }
                Result.Status.SUCCESS -> {
                    binder.loading = false
                    filterTags(binder.searchEt.text.toString())
                }
                else -> {
                    binder.loading = false
                    binder.error = "Unable to fetch Tags"
                }
            }
            binder.executePendingBindings()
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.slide_out_down)
    }

    private val _itemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemId: Int, position: Int) {
            val tag = tagListAdapter.tagList?.getOrNull(position)
            if (tag?.isNotEmpty() == true) {
                addChip(tag)
            }
            binder.searchEt.setText("")

        }

    }

    private fun addChip(text: String) {
        if (!viewModel.shouldAddTag(text)) {
            return
        }
        viewModel.addTag(text)
        createChip(text).apply { binder.tagChips.addView(this) }
        onChipUpdated()
    }

    private fun setupView() {
        binder.searchEt.requestFocus()
        viewModel.existingTags = intent?.getStringArrayListExtra(TAGS) ?: arrayListOf()
        tagListAdapter =
            TagListAdapter().apply { itemClickListener = _itemClickListener }
        binder.tagsRv.adapter = tagListAdapter

        //Chips
        onChipUpdated()
        viewModel.existingTags.forEach { tag ->
            createChip(tag).apply { binder.tagChips.addView(this) }
        }

        binder.searchEt.doAfterTextChanged {
            val query = it.toString()
            when {
                query.trim().isEmpty() -> {
                    filterTags(query)
                }
                query.endsWith(" ") -> {
                    addChip(query.removeSuffix(" "))
                    binder.searchEt.setText("")
                }
                else -> {
                    filterTags(query)
                }
            }
        }
        binder.searchEt.setOnEditorActionListener { v, actionId, event ->
            if (actionId == IME_ACTION_SEARCH) {
                hideKeyboard()
            }
            return@setOnEditorActionListener true
        }
        binder.doneTv.setOnClickListener {
            viewModel.saveNewTags()
            val i = Intent().apply { putStringArrayListExtra(TAGS, viewModel.existingTags) }
            setResult(RESULT_OK, i)
            finish()
        }
        binder.clickListener = _clickListener
    }

    private val _clickListener = View.OnClickListener { v ->
        if (v?.id == R.id.try_again_btn) {
            viewModel.fetchAllTags()
        }
    }

    private fun filterTags(query: String) {
        if (query.trim().isEmpty()) {
            tagListAdapter.update(viewModel.tags)
            return
        }
        val tags = viewModel.tags
            .filter { it?.toLowerCase()?.startsWith(query.toLowerCase()) == true }
        val newTags = ArrayList(tags)
        tagListAdapter.update(newTags)
    }

    private fun createChip(text: String?): Chip {
        val chip = layoutInflater
            .inflate(R.layout.tag_chip, binder.tagChips, false) as Chip
        chip.text = text
        chip.setOnCloseIconClickListener { removeChip(it as Chip) }
        return chip
    }

    private fun removeChip(chip: Chip) {
        viewModel.removeTag(chip.text.toString())
        binder.tagChips.removeView(chip)
        onChipUpdated()
    }

    private fun onChipUpdated() {
        val hasTags = viewModel.existingTags.isNotEmpty()
        binder.tagChips.visible(hasTags)
    }
}