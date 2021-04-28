package com.grocery.app.extensions

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

val authUser: FirebaseUser?
    get() = Firebase.auth.currentUser