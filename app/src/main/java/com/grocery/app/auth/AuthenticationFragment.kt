package com.grocery.app.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.grocery.app.databinding.FragmentAuthenticationBinding
import com.grocery.app.extensions.authUser
import com.grocery.app.extensions.showError
import com.grocery.app.extensions.showSuccess
import com.grocery.app.extras.Result
import com.grocery.app.viewModels.AuthViewModel


class AuthenticationFragment : Fragment() {

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
    }

    private fun observe() {
        viewModel.updateUserLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {

                }
                Result.Status.SUCCESS -> {
                    binder.root.showSuccess("User Info Updated.")
                }
                Result.Status.ERROR -> {
                    binder.root.showError("User info update failed")
                }
            }
        })
    }

    private val _user
        get() = viewModel.user

    private fun setupView() {
        binder.mobEt.setText(authUser?.phoneNumber)
        binder.nameEt.doAfterTextChanged { _user.name = it.toString() }
        binder.addressEt.doAfterTextChanged { _user.address = it.toString() }
        binder.btnLogout.setOnClickListener { viewModel.updateUserInfo() }

        _user.phone = authUser?.phoneNumber
        _user.id = authUser?.uid
        _user.name = authUser?.displayName
        _user.address = binder.addressEt.text.toString()

    }


}