package com.grocery.app.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.grocery.app.R
import com.grocery.app.constant.PRODUCT
import com.grocery.app.contracts.SelectTagContract
import com.grocery.app.databinding.ActivityAddProductBinding
import com.grocery.app.extensions.*
import com.grocery.app.extras.Result
import com.grocery.app.models.Category
import com.grocery.app.models.Product
import com.grocery.app.utils.isBlank
import com.grocery.app.viewModels.CategoryViewModel
import com.grocery.app.viewModels.ProductViewModel

class AddProductActivity : ImagePickerActivity(), View.OnClickListener {


    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 1009
    }

    private lateinit var binder: ActivityAddProductBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var catViewModel: CategoryViewModel

    private val _product
        get() = viewModel.product

    private val _editMode
        get() = !_product.id.isBlank()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_add_product)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        catViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        setupView()
        observe()
        catViewModel.fetchCategoryList()
    }


    private fun observe() {
        catViewModel.catListLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    loader(true)
                    changeFieldState(false)
                }
                Result.Status.SUCCESS -> {
                    loader(false)
                    changeFieldState(true)
                    viewModel.catList = it.data ?: arrayListOf()
                    setProductCategory()
                }
                Result.Status.ERROR -> {
                    loader(false)
                    changeFieldState(true)
                    binder.root.showError(getString(R.string.category_info_fetch_error))
                }
            }
        })

        viewModel.addOrUpdateProductLiveData.observe(this, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    loader(true)
                    changeFieldState(false)
                }
                Result.Status.SUCCESS -> {
                    loader(false)
                    changeFieldState(true)
                    onProductUpdated()
                }
                Result.Status.ERROR -> {
                    loader(false)
                    changeFieldState(true)
                    binder.root.showError(getString(R.string.create_product_error))
                }
            }
        })
    }

    private fun onProductUpdated() {
        setResult(Activity.RESULT_OK)
        onBackPressed()
    }

    private fun setProductCategory() {
        val items = arrayListOf<String>()
        var selectedCat = viewModel.catList.firstOrNull() ?: Category()
        for (idx in viewModel.catList.indices) {
            val item = viewModel.catList[idx]
            items.add(item.name ?: "")
            if (_product.categoryId == item.id) {
                selectedCat = item.clone() ?: Category()
            }
        }

        val catListAdapter = ArrayAdapter(this, R.layout.drop_down_item, items)
        binder.catTv.setAdapter(catListAdapter)
        if (viewModel.catList.isNotEmpty()) {
            binder.catTv.setText(selectedCat.name, false)
            _product.categoryId = selectedCat.id
        }
        binder.catTv.setOnItemClickListener { _, _, position, _ ->
            _product.categoryId = viewModel.catList[position].id
        }
    }

    private fun loader(show: Boolean) {
        binder.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun changeFieldState(active: Boolean) {
        binder.productIv.isEnabled = active
        binder.nameEt.isEnabled = active
        binder.descEt.isEnabled = active
        binder.priceEt.isEnabled = active
        binder.catTv.isEnabled = active
        binder.activeTv.isEnabled = active
        binder.addBtn.isEnabled = active
    }

    private val _selectTag =
        registerForActivityResult(SelectTagContract()) { result ->
            result?.let { rst ->
                _product.tags = rst
                onChipUpdated()
                binder.tagChips.removeAllViews()
                _product.tags?.forEach { tag ->
                    tag?.let {
                        createChip(it).apply { binder.tagChips.addView(this) }
                    }
                }
            }

        }

    @SuppressLint("DefaultLocale")
    private fun setupView() {
        intent?.extras?.getParcelable<Product>(PRODUCT)?.let {
            viewModel.product = it
            binder.nameEt.setText(it.name)
            binder.descEt.setText(it.description)
            binder.priceEt.setText("${it.price}")
            binder.discountEt.setText(it.discount?.toString())
        }
        //Toolbar
        binder.toolBar.title =
            getString(if (_editMode) R.string.edit_product else R.string.add_product)
        setupToolbar(binder.toolBar)
        binder.addBtn.text = getString(if (_editMode) R.string.update else R.string.add)
        binder.productIv.setOnClickListener(this)
        binder.addBtn.setOnClickListener(this)
        binder.nameEt.addTextChangedListener { _product.name = it.toString() }
        binder.descEt.addTextChangedListener { _product.description = it.toString() }
        binder.priceEt.addTextChangedListener { _product.price = it.toString().toDouble() }
        binder.discountEt.addTextChangedListener {
            _product.discount = it.toString().toDoubleOrNull()
        }
        loadProductImage()

        //active
        val activeItems = resources.getStringArray(R.array.product_active)
        val activeAdapter = ArrayAdapter(this, R.layout.drop_down_item, activeItems)
        binder.activeTv.setAdapter(activeAdapter)
        binder.activeTv.setOnItemClickListener { _, _, position, _ ->
            _product.active = java.lang.Boolean.parseBoolean(activeItems[position])
        }
        binder.activeTv.setText(_product.active?.toString()?.capitalize() ?: activeItems[0], false)

        //Chips
        val tags = _product.tags?.filterNotNull() ?: arrayListOf()
        onChipUpdated()
        tags.forEach { tag ->
            createChip(tag).apply { binder.tagChips.addView(this) }
        }
        binder.addTagBtn.setOnClickListener {
            _selectTag.launch(_product.tags)
        }

    }

    private fun onChipUpdated() {
        val isTagEmpty = _product.tags?.isEmpty() != false
        binder.tagChips.visible(!isTagEmpty)
        binder.addTagTxt.text =
            getString(if (isTagEmpty) R.string.add_tag else R.string.add_more_tags)
    }

    private fun createChip(text: String): Chip {
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


    override fun onImagePicked(requestCode: Int, uri: Uri) {
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            _product.url = uri.toString()
            loadProductImage()
        }
    }

    private fun loadProductImage() {
        binder.productIv.loadImage(
            url = _product.url,
            circular = true,
            placeholder = R.drawable.category_placeholder
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.product_iv -> startPickerActivity(PICK_IMAGE_REQUEST_CODE)
            R.id.add_btn -> onAddBtnClick()
        }
    }

    private fun onAddBtnClick() {
        if (validate()) {
            viewModel.addOrUpdateProduct()
        }
    }

    private fun validate(): Boolean {
        var messageId = -1
        if (_product.name.isBlank()) {
            messageId = R.string.product_name_error
        } else if (_product.price == null || _product.price!! <= 0) {
            messageId = R.string.product_price_error
        } else if (_product.categoryId.isBlank()) {
            messageId = R.string.choose_product_category
        }
        if (messageId > -1) {
            binder.root.showError(getString(messageId))
        }
        return messageId == -1
    }
}