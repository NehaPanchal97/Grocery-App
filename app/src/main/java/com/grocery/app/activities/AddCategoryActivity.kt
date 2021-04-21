package com.grocery.app.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.grocery.app.R
import com.grocery.app.databinding.ActivityAddCategoryBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extensions.toInt
import com.grocery.app.extras.Result
import com.grocery.app.extras.Result.Status
import com.grocery.app.utils.isBlank
import com.grocery.app.viewModels.CategoryViewModel

class AddCategoryActivity : ImagePickerActivity() {

    lateinit var binder: ActivityAddCategoryBinding
    lateinit var viewModel: CategoryViewModel

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 121
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_add_category)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        setupView()
        observe()
    }

    private fun observe() {
        viewModel.addCatLiveData.observe(this, Observer {
            when (it.type) {
                Status.LOADING -> {
                    updateInteraction(false)
                    binder.progressBar.show()
                }
                Status.SUCCESS -> {
                    updateInteraction(true)
                    binder.progressBar.hide()
                    binder.root.showSuccess("Category Added successfully!")
                }
                else -> {
                    updateInteraction(true)
                    binder.progressBar.hide()
                    binder.root.showError("Error Creating Category!")
                }

            }
        })
    }

    fun updateInteraction(enable: Boolean) {
        binder.categoryIv.isEnabled = enable
        binder.submitBtn.isEnabled = enable
        binder.nameEt.isEnabled = enable
        binder.rankEt.isEnabled = enable
    }

    private fun setupView() {
        binder.categoryIv.setOnClickListener {
            startPickerActivity(PICK_IMAGE_REQUEST_CODE)
        }
        binder.submitBtn.setOnClickListener {
            if (validateFields()) {
                addCategory()
            }
        }
        loadCategoryImage()
        binder.toolBar.inflateMenu(R.menu.add_category_menu)
        binder.toolBar.setOnMenuItemClickListener {
            if (it.itemId == R.id.category_list) {
                startActivity(Intent(this, CategoryListActivity::class.java))
                return@setOnMenuItemClickListener true
            }
            return@setOnMenuItemClickListener false
        }
    }

    fun addCategory() {
        viewModel.addCategory(
            binder.nameEt.text.toString(),
            binder.rankEt.text.toString().toInt()
        )
    }

    override fun onImagePicked(requestCode: Int, uri: Uri) {
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            viewModel.categoryImage = uri.toString()
            loadCategoryImage()
        }
    }

    fun loadCategoryImage() {
        binder.categoryIv.loadImage(
            url = viewModel.categoryImage,
            circular = true,
            placeholder = R.drawable.category_placeholder
        )
    }

    fun validateFields(): Boolean {
        var validated = true
        if (binder.nameEt.text.toString().isBlank()) {
            binder.nameField.error = getString(R.string.cate_name_error)
            validated = false
        } else if (binder.rankEt.text.toString().isBlank()) {
            binder.rankField.error = getString(R.string.cat_rank_error)
            validated = false
        }
        return validated
    }
}