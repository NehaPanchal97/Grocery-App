package com.grocery.app.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.HomePage.HomePageActivity
import com.grocery.app.constant.USER
import com.grocery.app.databinding.FragmentAuthenticationBinding
import com.grocery.app.extensions.authUser
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extensions.showToast
import com.grocery.app.extras.Result
import com.grocery.app.models.User
import com.grocery.app.utils.PrefManager
import com.grocery.app.viewModels.AuthViewModel


class UpdateProfileFragment : Fragment() {
    private val prefManager by lazy { PrefManager.getInstance(requireContext()) }

    private lateinit var binder: FragmentAuthenticationBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binder = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return binder.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        setupView()
        observe()
//        setUpViewWithData()
    }

    private fun observe() {
        viewModel.updateUserLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                }
                Result.Status.SUCCESS -> {
                    val updateRequest = prefManager.contains(USER)
                    prefManager.put(USER, _user)
                    if (updateRequest){
                        activity?.setResult(Activity.RESULT_OK)
                        context?.showToast("Profile Updated Successfully")
                    }
                    else{
                        context?.showToast("Profile Created Successfully")
                        startActivity(Intent(requireContext(), HomePageActivity::class.java))
                    }
                    activity?.finish()
                }
                Result.Status.ERROR -> {
                    binder.root.showError("User info update failed")
                }
            }
        })
    }

    private var _user
        get() = viewModel.user
        set(value) {
            viewModel.user = value
        }

    private fun setupView() {
        _user = createUserFromAuth()
        binder.mobEt.setText(_user?.phone)
        binder.nameEt.setText(_user?.name)
        binder.addressEt.setText(_user?.address)
        binder.nameEt.doAfterTextChanged { _user?.name = it.toString() }
        binder.addressEt.doAfterTextChanged { _user?.address = it.toString() }
        binder.btnSave.setOnClickListener { viewModel.updateUserInfo() }


//        _user?.phone = authUser?.phoneNumber
//        _user?.id = authUser?.uid
//        _user?.name = authUser?.displayName
//        _user?.address = binder.addressEt.text.toString()

    }

    private fun createUserFromAuth(): User? {
        return PrefManager.getInstance(requireContext()).get<User>(USER) ?: User(
            id = authUser?.uid,
            phone = authUser?.phoneNumber,
            name = authUser?.displayName,
            url = authUser?.photoUrl?.toString()
        )
    }



//    private fun setUpViewWithData() {
//        binder.nameEt.setText(authUser?.displayName)
//        binder.addressEt.setText(binder.addressEt.text.toString())
////        viewModel.fetchUserInfo()
//    }


}