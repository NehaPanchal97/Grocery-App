package com.grocery.app.activities

import android.os.Bundle
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

class SelectTagActivity : BaseActivity() {

    private lateinit var binder: ActivitySelectTagBinding
    private lateinit var viewModel: TagViewModel
    private lateinit var tagListAdapter: TagListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_select_tag)
        viewModel = ViewModelProvider(this).get(TagViewModel::class.java)
        setupView()
        observe()
    }

    private fun observe() {
        viewModel.tagListLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.SUCCESS -> {
                    tagListAdapter.update(it.data)
                }
                else -> {

                }
            }
        })
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

        //
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
            val length = it?.toString()?.trim()?.length ?: 0
            if (length == 0) {
                tagListAdapter.update(null)
            } else if (length > 2) {
                viewModel.fetchTags(it?.toString())
            }
            binder.addTv.visible(length > 0)
        }
        binder.searchEt.setOnEditorActionListener { v, actionId, event ->
            if (actionId == IME_ACTION_SEARCH) {
                hideKeyboard()
            }
            return@setOnEditorActionListener true
        }
        binder.addTv.setOnClickListener {
            addChip(binder.searchEt.text.toString())
            binder.searchEt.setText("")
        }
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