package com.grocery.app.activities

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.R
import com.grocery.app.constant.CATEGORY
import com.grocery.app.databinding.ActivityAddCategoryBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extensions.toInt
import com.grocery.app.extras.Result.Status
import com.grocery.app.models.Category
import com.grocery.app.utils.isBlank
import com.grocery.app.viewModels.CategoryViewModel

class AddCategoryActivity : ImagePickerActivity() {

    private lateinit var binder: ActivityAddCategoryBinding
    private lateinit var viewModel: CategoryViewModel

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 121
    }

    private var _category
        get() = viewModel.category
        set(value) {
            viewModel.category = value
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
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                else -> {
                    updateInteraction(true)
                    binder.progressBar.hide()
                    binder.root.showError(getString(R.string.cat_creation_error_msg))
                }

            }
        })
    }

    private fun updateInteraction(enable: Boolean) {
        binder.categoryIv.isEnabled = enable
        binder.submitBtn.isEnabled = enable
        binder.nameEt.isEnabled = enable
        binder.rankEt.isEnabled = enable
    }

    private fun setupView() {
        intent?.extras?.getParcelable<Category>(CATEGORY)?.let {
            _category = it
            binder.nameEt.setText(it.name)
            binder.rankEt.setText("${it.rank}")
        }
        val editMode = !_category.id.isBlank()
        binder.submitBtn.text = getString(if (editMode) R.string.update else R.string.add)
        binder.categoryIv.setOnClickListener {
            startPickerActivity(PICK_IMAGE_REQUEST_CODE)
        }
        binder.submitBtn.setOnClickListener {
            if (validateFields()) {
                viewModel.addCategory()
            }
        }
        binder.nameEt.doAfterTextChanged { _category.name = it.toString() }
        binder.rankEt.doAfterTextChanged { _category.rank = it.toString().toInt() }
        loadCategoryImage()
        setupToolbar(binder.toolBar)
    }

    override fun onImagePicked(requestCode: Int, uri: Uri) {
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            _category.url = uri.toString()
            loadCategoryImage()
        }
    }

    private fun loadCategoryImage() {
        binder.categoryIv.loadImage(
            url = _category.url,
            circular = true,
            placeholder = R.drawable.category_placeholder
        )
    }

    private fun validateFields(): Boolean {
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