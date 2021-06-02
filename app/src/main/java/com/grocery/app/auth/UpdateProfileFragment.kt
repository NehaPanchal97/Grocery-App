package com.grocery.app.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grocery.app.constant.UPDATE_PROFILE
import com.grocery.app.homePage.HomePageActivity
import com.grocery.app.constant.USER
import com.grocery.app.databinding.FragmentAuthenticationBinding
import com.grocery.app.extensions.*
import com.grocery.app.extras.Result
import com.grocery.app.fragments.ImagePickerFragment
import com.grocery.app.listeners.OnProfileUpdatedListener
import com.grocery.app.models.User
import com.grocery.app.utils.PrefManager
import com.grocery.app.utils.signOut
import com.grocery.app.viewModels.AuthViewModel
import kotlinx.android.synthetic.main.fragment_authentication.*


class UpdateProfileFragment : ImagePickerFragment() {
    private val prefManager by lazy { PrefManager.getInstance(requireContext()) }

    private lateinit var binder: FragmentAuthenticationBinding
    private lateinit var viewModel: AuthViewModel

    companion object {
        const val PROFILE_PICK_REQUEST_CODE = 345
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binder = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return binder.root
    }

    override fun onImagePicked(requestCode: Int, uri: Uri) {
        if (requestCode == PROFILE_PICK_REQUEST_CODE) {
            _user?.url = uri.toString()
            loadProfile()
        }
    }

    private fun loadProfile() {
        binder.profilePic.loadImage(_user?.url)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        setupView()
        observe()
        binder.btnLogout.setOnClickListener {
            context?.showToast("Logging Out")
            context?.signOut()
            startActivity(Intent(context, SsoLoginActivity::class.java))
            activity?.finishAffinity()
        }
    }

    private fun observe() {
        viewModel.updateUserLiveData.observe(viewLifecycleOwner, Observer {
            when (it.type) {
                Result.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                Result.Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    val updateRequest = prefManager.contains(USER)
                    prefManager.put(USER, _user)
                    if (updateRequest) {
                        context?.showToast("Profile Updated Successfully")
                        activity?.cast<OnProfileUpdatedListener>()?.onProfileUpdated()
                    } else {
                        context?.showToast("Profile Created Successfully")
                        startActivity(Intent(requireContext(), HomePageActivity::class.java))
                        activity?.finish()
                    }
                }
                Result.Status.ERROR -> {
                    progressBar.visibility = View.GONE
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
        loadProfile()
        binder.nameEt.doAfterTextChanged { _user?.name = it.toString() }
        binder.addressEt.doAfterTextChanged { _user?.address = it.toString() }
        binder.btnSave.setOnClickListener {
            viewModel.updateUser()
            fireUpdateProfileEvent()
        }
        binder.uploadImage.setOnClickListener { startPickerActivity(PROFILE_PICK_REQUEST_CODE) }
    }

    private fun createUserFromAuth(): User? {
        return PrefManager.getInstance(requireContext()).get<User>(USER) ?: User(
            id = authUser?.uid,
            phone = authUser?.phoneNumber,
            name = authUser?.displayName,
            url = authUser?.photoUrl?.toString()
        )
    }

    private fun fireUpdateProfileEvent() {
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(Intent(UPDATE_PROFILE))
    }

}